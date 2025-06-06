// 全局变量
let currentUser = null;
let userAuthority = 0;

// API配置
const API_BASE_URL = 'http://localhost:3000/api';

// 应用初始化
document.addEventListener('DOMContentLoaded', function() {
    initializeApp();
    setupEventListeners();
});

function initializeApp() {
    // 检查是否有保存的登录状态
    const savedUser = localStorage.getItem('currentUser');
    if (savedUser) {
        currentUser = JSON.parse(savedUser);
        showMainInterface();
        loadDashboardData();
    } else {
        showLogin();
    }
}

function setupEventListeners() {
    // 登录表单
    document.getElementById('login-form').addEventListener('submit', handleLogin);
    
    // 注册表单
    document.getElementById('register-form').addEventListener('submit', handleRegister);
    
    // 产品表单
    document.getElementById('product-form').addEventListener('submit', handleProductSubmit);
    
    // 修改密码表单
    document.getElementById('password-form').addEventListener('submit', handlePasswordChange);
    
    // 搜索功能
    document.getElementById('product-search').addEventListener('keypress', function(e) {
        if (e.key === 'Enter') {
            searchProducts();
        }
    });

    // 模态框外点击关闭
    window.addEventListener('click', function(e) {
        if (e.target.classList.contains('modal')) {
            e.target.style.display = 'none';
        }
    });
}

// HTTP API 调用函数
async function sendRequest(requestData) {
    showLoading();
    
    try {
        let url = '';
        let method = 'POST';
        let body = null;
        
        // 根据请求类型构建API调用
        switch (requestData.function) {
            case 'login':
                url = `${API_BASE_URL}/login`;
                body = JSON.stringify({
                    username: requestData.username,
                    password: requestData.password
                });
                break;
                
            case 'register':
                url = `${API_BASE_URL}/register`;
                body = JSON.stringify({
                    username: requestData.username,
                    password: requestData.password,
                    authority: requestData.authority || 1
                });
                break;
                
            case 'getAllProducts':
                url = `${API_BASE_URL}/products`;
                method = 'GET';
                break;            case 'insertProduct':
                url = `${API_BASE_URL}/products`;
                body = JSON.stringify({
                    name: requestData.name,
                    price: requestData.price,
                    stock: requestData.stock,
                    cas: requestData.cas,
                    formula: requestData.formula,
                    category: requestData.category,
                    structureImage: requestData.structureImage || ''
                });
                break;
                
            case 'updateProduct':
                url = `${API_BASE_URL}/products/${requestData.id}`;
                method = 'PUT';
                body = JSON.stringify({
                    name: requestData.name,
                    price: requestData.price,
                    stock: requestData.stock,
                    cas: requestData.cas,
                    formula: requestData.formula,
                    category: requestData.category,
                    structureImage: requestData.structureImage || ''
                });
                break;
                
            case 'deleteProduct':
                url = `${API_BASE_URL}/products/${requestData.id}`;
                method = 'DELETE';
                break;
                
            case 'getAllUsers':
                url = `${API_BASE_URL}/users`;
                method = 'GET';
                break;
                
            case 'deleteUser':
                url = `${API_BASE_URL}/users/${requestData.username}`;
                method = 'DELETE';
                break;
                
            case 'changePassword':
                url = `${API_BASE_URL}/password`;
                method = 'PUT';
                body = JSON.stringify({
                    username: requestData.username,
                    oldPassword: requestData.oldPassword,
                    newPassword: requestData.newPassword
                });
                break;
                
            case 'searchProducts':
                url = `${API_BASE_URL}/search/${encodeURIComponent(requestData.keyword)}`;
                method = 'GET';
                break;
                
            default:
                throw new Error(`Unknown function: ${requestData.function}`);
        }
        
        const options = {
            method: method,
            headers: {
                'Content-Type': 'application/json',
            }
        };
        
        if (body) {
            options.body = body;
        }
        
        const response = await fetch(url, options);
        const data = await response.json();
        
        // 转换响应格式以保持兼容性
        if (data.success) {
            if (requestData.function === 'login') {
                return {
                    status: "1",
                    message: "登录成功",
                    authority: data.user.authority
                };
            } else if (requestData.function === 'getAllProducts' || requestData.function === 'searchProducts') {
                return {
                    status: "1",
                    message: "查询成功",
                    products: data.products
                };
            } else if (requestData.function === 'getAllUsers') {
                return {
                    status: "1",
                    message: "查询成功",
                    users: data.users
                };
            } else {
                return {
                    status: "1",
                    message: data.message || "操作成功"
                };
            }
        } else {
            return {
                status: "0",
                message: data.message || "操作失败"
            };
        }
        
    } catch (error) {
        console.error('API请求失败:', error);
        throw error;
    } finally {
        hideLoading();
    }
}

// 登录处理
async function handleLogin(e) {
    e.preventDefault();
    
    const username = document.getElementById('login-username').value;
    const password = document.getElementById('login-password').value;
    
    if (!username || !password) {
        showMessage('请输入用户名和密码', 'error');
        return;
    }
    
    try {
        const response = await sendRequest({
            function: 'login',
            username: username,
            password: password
        });
        
        if (response.status === "1") {
            currentUser = {
                username: username,
                authority: response.authority
            };
            userAuthority = response.authority;
            
            // 保存登录状态
            localStorage.setItem('currentUser', JSON.stringify(currentUser));
            
            showMessage('登录成功！', 'success');
            showMainInterface();
            loadDashboardData();
        } else {
            showMessage(response.message || '登录失败', 'error');
        }
    } catch (error) {
        showMessage('连接服务器失败，请检查服务器状态', 'error');
        console.error('登录错误:', error);
    }
}

// 注册处理
async function handleRegister(e) {
    e.preventDefault();
    
    const username = document.getElementById('register-username').value;
    const password = document.getElementById('register-password').value;
    const confirmPassword = document.getElementById('register-confirm-password').value;
    
    if (!username || !password || !confirmPassword) {
        showMessage('请填写所有字段', 'error');
        return;
    }
    
    if (password !== confirmPassword) {
        showMessage('两次输入的密码不一致', 'error');
        return;
    }
    
    if (password.length < 6) {
        showMessage('密码长度至少6位', 'error');
        return;
    }
    
    try {
        const response = await sendRequest({
            function: 'register',
            username: username,
            password: password
        });
        
        if (response.status === "1") {
            showMessage('注册成功！请登录', 'success');
            showLogin();
            // 清空注册表单
            document.getElementById('register-form').reset();
        } else {
            showMessage(response.message || '注册失败', 'error');
        }
    } catch (error) {
        showMessage('连接服务器失败', 'error');
        console.error('注册错误:', error);
    }
}

// 显示界面
function showLogin() {
    document.getElementById('login-container').style.display = 'flex';
    document.getElementById('register-container').style.display = 'none';
    document.getElementById('main-container').style.display = 'none';
}

function showRegister() {
    document.getElementById('login-container').style.display = 'none';
    document.getElementById('register-container').style.display = 'flex';
    document.getElementById('main-container').style.display = 'none';
}

function showMainInterface() {
    document.getElementById('login-container').style.display = 'none';
    document.getElementById('register-container').style.display = 'none';
    document.getElementById('main-container').style.display = 'grid';
    
    // 更新用户信息显示
    document.getElementById('current-user').textContent = `欢迎, ${currentUser.username}`;
    
    // 根据权限显示/隐藏用户管理
    const userManagementLink = document.getElementById('user-management-link');
    if (userAuthority >= 1) {
        userManagementLink.style.display = 'flex';
    } else {
        userManagementLink.style.display = 'none';
    }
    
    // 默认显示仪表板
    showSection('dashboard');
}

// 退出登录
function logout() {
    currentUser = null;
    userAuthority = 0;
    localStorage.removeItem('currentUser');
    showLogin();
    showMessage('已安全退出', 'info');
}

// 显示不同的内容区域
function showSection(sectionName) {
    // 隐藏所有内容区域
    const sections = document.querySelectorAll('.content-section');
    sections.forEach(section => section.classList.remove('active'));
    
    // 移除所有导航项的活动状态
    const navItems = document.querySelectorAll('.nav-item');
    navItems.forEach(item => item.classList.remove('active'));
    
    // 显示选定的内容区域
    document.getElementById(`${sectionName}-section`).classList.add('active');
    
    // 设置对应导航项为活动状态
    event.target.classList.add('active');
    
    // 根据不同区域加载对应数据
    switch(sectionName) {
        case 'dashboard':
            loadDashboardData();
            break;
        case 'products':
            loadProducts();
            break;
        case 'users':
            if (userAuthority >= 1) {
                loadUsers();
            }
            break;
    }
}

// 加载仪表板数据
async function loadDashboardData() {
    try {
        // 加载产品数据用于统计
        const productResponse = await sendRequest({ function: 'getAllProducts' });
        if (productResponse.status === "1") {
            const products = productResponse.products;
            document.getElementById('total-products').textContent = products.length;
            
            // 计算总价值
            const totalValue = products.reduce((sum, product) => sum + (product.price * product.stock), 0);
            document.getElementById('total-value').textContent = `¥${totalValue.toFixed(2)}`;
        }
        
        // 加载用户数据用于统计
        if (userAuthority >= 1) {
            const userResponse = await sendRequest({ function: 'getAllUsers' });
            if (userResponse.status === "1") {
                document.getElementById('total-users').textContent = userResponse.users.length;
            }
        }
    } catch (error) {
        console.error('加载仪表板数据失败:', error);
    }
}

// 加载产品列表
async function loadProducts() {
    try {
        const response = await sendRequest({ function: 'getAllProducts' });
        
        if (response.status === "1") {
            displayProducts(response.products);
        } else {
            showMessage('加载产品列表失败', 'error');
        }
    } catch (error) {
        showMessage('连接服务器失败', 'error');
        console.error('加载产品失败:', error);
    }
}

// 显示产品列表
function displayProducts(products) {
    const tbody = document.getElementById('products-table-body');
    tbody.innerHTML = '';
    
    if (products.length === 0) {
        tbody.innerHTML = '<tr><td colspan="8" class="text-center">暂无产品数据</td></tr>';
        return;
    }
    
    products.forEach(product => {
        const row = document.createElement('tr');
        
        const categoryNames = ['有机化合物', '无机化合物', '生物试剂', '分析试剂'];
        const categoryName = categoryNames[parseInt(product.category)] || '未知';
        
        row.innerHTML = `
            <td>${product.id}</td>
            <td>${product.name}</td>
            <td>¥${product.price.toFixed(2)}</td>
            <td>${product.stock}</td>
            <td>${product.cas || '-'}</td>
            <td>${product.formula || '-'}</td>
            <td>${categoryName}</td>
            <td>
                <div class="action-buttons">
                    <button class="btn-sm btn-edit" onclick="editProduct(${product.id})">
                        <i class="fas fa-edit"></i> 编辑
                    </button>
                    <button class="btn-sm btn-delete" onclick="deleteProduct(${product.id})">
                        <i class="fas fa-trash"></i> 删除
                    </button>
                </div>
            </td>
        `;
        
        tbody.appendChild(row);
    });
}

// 搜索产品
async function searchProducts() {
    const searchTerm = document.getElementById('product-search').value.trim();
    
    if (!searchTerm) {
        loadProducts();
        return;
    }
    
    try {
        // 先获取所有产品，然后在前端过滤（实际应用中应该在后端搜索）
        const response = await sendRequest({ function: 'getAllProducts' });
        
        if (response.status === "1") {
            const filteredProducts = response.products.filter(product => 
                product.name.toLowerCase().includes(searchTerm.toLowerCase()) ||
                (product.cas && product.cas.includes(searchTerm))
            );
            
            displayProducts(filteredProducts);
            
            if (filteredProducts.length === 0) {
                showMessage('未找到匹配的产品', 'info');
            }
        }
    } catch (error) {
        showMessage('搜索失败', 'error');
        console.error('搜索产品失败:', error);
    }
}

// 显示添加产品模态框
function showAddProduct() {
    document.getElementById('product-modal-title').textContent = '添加产品';
    document.getElementById('product-form').reset();
    document.getElementById('product-id').value = '';
    document.getElementById('product-modal').style.display = 'block';
}

// 编辑产品
async function editProduct(productId) {
    try {
        // 获取产品详情
        const response = await sendRequest({ 
            function: 'queryProductById',
            id: productId 
        });
        
        if (response.status === "1") {
            const product = response.product;
            
            document.getElementById('product-modal-title').textContent = '编辑产品';
            document.getElementById('product-id').value = product.id;
            document.getElementById('product-name').value = product.name;
            document.getElementById('product-price').value = product.price;
            document.getElementById('product-stock').value = product.stock;
            document.getElementById('product-cas').value = product.cas || '';
            document.getElementById('product-formula').value = product.formula || '';
            document.getElementById('product-category').value = product.category || '0';
            document.getElementById('product-structure').value = product.structurePictureAddress || '';
            
            document.getElementById('product-modal').style.display = 'block';
        } else {
            showMessage('获取产品信息失败', 'error');
        }
    } catch (error) {
        showMessage('加载产品信息失败', 'error');
        console.error('编辑产品失败:', error);
    }
}

// 删除产品
async function deleteProduct(productId) {
    if (!confirm('确定要删除这个产品吗？此操作不可撤销。')) {
        return;
    }
    
    try {
        const response = await sendRequest({
            function: 'deleteProduct',
            id: productId.toString()
        });
        
        if (response.status === "1") {
            showMessage('产品删除成功', 'success');
            loadProducts(); // 重新加载产品列表
        } else {
            showMessage(response.message || '删除失败', 'error');
        }
    } catch (error) {
        showMessage('删除操作失败', 'error');
        console.error('删除产品失败:', error);
    }
}

// 处理产品表单提交
async function handleProductSubmit(e) {
    e.preventDefault();
    
    const productId = document.getElementById('product-id').value;
    const productData = {
        name: document.getElementById('product-name').value,
        price: parseFloat(document.getElementById('product-price').value),
        stock: parseInt(document.getElementById('product-stock').value),
        cas: document.getElementById('product-cas').value,
        formula: document.getElementById('product-formula').value,
        category: document.getElementById('product-category').value,
        structurePictureAddress: document.getElementById('product-structure').value
    };
    
    // 验证数据
    if (!productData.name || productData.price < 0 || productData.stock < 0) {
        showMessage('请填写正确的产品信息', 'error');
        return;
    }
    
    try {
        let response;
          if (productId) {
            // 更新产品
            response = await sendRequest({
                function: 'updateProduct',
                id: parseInt(productId),
                name: productData.name,
                price: productData.price,
                stock: productData.stock,
                cas: productData.cas,
                formula: productData.formula,
                category: productData.category,
                structureImage: productData.structurePictureAddress
            });        } else {
            // 添加新产品
            response = await sendRequest({
                function: 'insertProduct',
                name: productData.name,
                price: productData.price,
                stock: productData.stock,
                cas: productData.cas,
                formula: productData.formula,
                category: productData.category,
                structureImage: productData.structurePictureAddress
            });
        }
        
        if (response.status === "1") {
            showMessage(productId ? '产品更新成功' : '产品添加成功', 'success');
            closeModal('product-modal');
            loadProducts(); // 重新加载产品列表
        } else {
            showMessage(response.message || '操作失败', 'error');
        }
    } catch (error) {
        showMessage('操作失败', 'error');
        console.error('产品操作失败:', error);
    }
}

// 加载用户列表
async function loadUsers() {
    if (userAuthority < 1) {
        showMessage('权限不足', 'error');
        return;
    }
    
    try {
        const response = await sendRequest({ function: 'getAllUsers' });
        
        if (response.status === "1") {
            displayUsers(response.users);
        } else {
            showMessage('加载用户列表失败', 'error');
        }
    } catch (error) {
        showMessage('连接服务器失败', 'error');
        console.error('加载用户失败:', error);
    }
}

// 显示用户列表
function displayUsers(users) {
    const tbody = document.getElementById('users-table-body');
    tbody.innerHTML = '';
    
    if (users.length === 0) {
        tbody.innerHTML = '<tr><td colspan="3" class="text-center">暂无用户数据</td></tr>';
        return;
    }
    
    users.forEach(user => {
        const row = document.createElement('tr');
        
        const authorityText = user.authority === 1 ? '管理员' : '普通用户';
        
        row.innerHTML = `
            <td>${user.username}</td>
            <td>${authorityText}</td>
            <td>
                <div class="action-buttons">
                    <button class="btn-sm btn-edit" onclick="changeUserAuthority('${user.username}', ${user.authority})">
                        <i class="fas fa-user-cog"></i> 权限
                    </button>
                    ${user.username !== currentUser.username ? 
                        `<button class="btn-sm btn-delete" onclick="deleteUser('${user.username}')">
                            <i class="fas fa-trash"></i> 删除
                        </button>` : ''
                    }
                </div>
            </td>
        `;
        
        tbody.appendChild(row);
    });
}

// 修改用户权限
async function changeUserAuthority(username, currentAuthority) {
    const newAuthority = currentAuthority === 1 ? 0 : 1;
    const authorityText = newAuthority === 1 ? '管理员' : '普通用户';
    
    if (!confirm(`确定要将用户 ${username} 的权限更改为 ${authorityText} 吗？`)) {
        return;
    }
    
    try {
        const response = await sendRequest({
            function: 'changeAuthority',
            username: username,
            authority: newAuthority
        });
        
        if (response.status === "1") {
            showMessage('权限修改成功', 'success');
            loadUsers(); // 重新加载用户列表
        } else {
            showMessage(response.message || '权限修改失败', 'error');
        }
    } catch (error) {
        showMessage('权限修改失败', 'error');
        console.error('修改权限失败:', error);
    }
}

// 删除用户
async function deleteUser(username) {
    if (!confirm(`确定要删除用户 ${username} 吗？此操作不可撤销。`)) {
        return;
    }
    
    try {
        const response = await sendRequest({
            function: 'deleteUser',
            username: username
        });
        
        if (response.status === "1") {
            showMessage('用户删除成功', 'success');
            loadUsers(); // 重新加载用户列表
        } else {
            showMessage(response.message || '删除失败', 'error');
        }
    } catch (error) {
        showMessage('删除操作失败', 'error');
        console.error('删除用户失败:', error);
    }
}

// 显示修改密码模态框
function showChangePassword() {
    document.getElementById('password-modal').style.display = 'block';
}

// 处理密码修改
async function handlePasswordChange(e) {
    e.preventDefault();
    
    const oldPassword = document.getElementById('old-password').value;
    const newPassword = document.getElementById('new-password').value;
    const confirmNewPassword = document.getElementById('confirm-new-password').value;
    
    if (!oldPassword || !newPassword || !confirmNewPassword) {
        showMessage('请填写所有字段', 'error');
        return;
    }
    
    if (newPassword !== confirmNewPassword) {
        showMessage('新密码两次输入不一致', 'error');
        return;
    }
    
    if (newPassword.length < 6) {
        showMessage('新密码长度至少6位', 'error');
        return;
    }
    
    try {
        const response = await sendRequest({
            function: 'changePassword',
            username: currentUser.username,
            oldPassword: oldPassword,
            newPassword: newPassword
        });
        
        if (response.status === "1") {
            showMessage('密码修改成功', 'success');
            closeModal('password-modal');
            document.getElementById('password-form').reset();
        } else {
            showMessage(response.message || '密码修改失败', 'error');
        }
    } catch (error) {
        showMessage('密码修改失败', 'error');
        console.error('修改密码失败:', error);
    }
}

// 关闭模态框
function closeModal(modalId) {
    document.getElementById(modalId).style.display = 'none';
}

// 显示加载动画
function showLoading() {
    document.getElementById('loading').style.display = 'flex';
}

// 隐藏加载动画
function hideLoading() {
    document.getElementById('loading').style.display = 'none';
}

// 显示消息提示
function showMessage(message, type = 'info') {
    const messageContainer = document.getElementById('message-container');
    
    const messageDiv = document.createElement('div');
    messageDiv.className = `message ${type}`;
    
    const icon = type === 'success' ? 'fas fa-check-circle' : 
                 type === 'error' ? 'fas fa-exclamation-circle' : 
                 'fas fa-info-circle';
    
    messageDiv.innerHTML = `
        <i class="${icon}"></i>
        <span>${message}</span>
    `;
    
    messageContainer.appendChild(messageDiv);
    
    // 3秒后自动移除消息
    setTimeout(() => {
        if (messageDiv.parentNode) {
            messageDiv.parentNode.removeChild(messageDiv);
        }
    }, 3000);
}
