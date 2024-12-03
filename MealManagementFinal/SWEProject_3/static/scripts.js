document.addEventListener('DOMContentLoaded', () => {
    const loginModal = document.getElementById('loginModal');
    const signinModal = document.getElementById('signinModal');
    const uploadModal = document.getElementById('uploadModal');
    const modifyModal = document.getElementById('modifyModal');
    const imageModal = document.getElementById('imageModal');
    const modalImage = document.getElementById('modalImage');
    const closeImageModal = document.getElementById('closeImageModal');
    const closeModify = document.getElementById('closeModify');

    const loginBtn = document.getElementById('login');
    const signinBtn = document.getElementById('signin');
    const uploadBtn = document.getElementById('upload');
    const logoutBtns = document.querySelectorAll('#logout');

    const pageTitle = document.getElementById('pageTitle');
    const homeIcon = document.getElementById('homeIcon');
    const searchButton = document.getElementById('searchButton');
    const searchKeyword = document.getElementById('searchKeyword');

    const closeLogin = document.getElementById('closeLogin');
    const closeSignin = document.getElementById('closeSignin');
    const closeUpload = document.getElementById('closeUpload');
    const closeAlert = document.getElementById('closeAlert');

    const loginMessage = document.getElementById('loginMessage');
    const signinMessage = document.getElementById('signinMessage');
    const uploadMessage = document.getElementById('uploadMessage');
    const modifyMessage = document.getElementById('modifyMessage');
    const alertBox = document.getElementById('alertBox');
    const alertMessage = document.getElementById('alertMessage');

    const loginForm = document.getElementById('loginForm');
    const signinForm = document.getElementById('signinForm');
    const uploadForm = document.getElementById('uploadForm');
    const modifyForm = document.getElementById('modifyForm');
    const photoInput = document.getElementById('photoInput');
    const photoDescription = document.getElementById('photoDescription');
    const photoKeywords = document.getElementById('photoKeywords');
    const photoPreview = document.getElementById('photoPreview');
    const modifyDescription = document.getElementById('modifyDescription');
    const modifyKeywords = document.getElementById('modifyKeywords');
    const photoContainer = document.getElementById('photoContainer');
    const searchResults = document.createElement('div'); 
    searchResults.id = 'searchResults'; 

    const notLoginedNav = document.querySelector('.not-logined_nav');
    const loginedNav = document.querySelector('.logined_nav');
    const welcomeMessage = document.getElementById('welcomeMessage');
    const nicknameDisplay = document.getElementById('nicknameDisplay');

    const userListContainer = document.getElementById('userListContainer');
    const userList = document.getElementById('userList');

    const messageListContainer = document.getElementById('messageListContainer');

    let currentUser = null;
    let allPhotos = JSON.parse(localStorage.getItem('all_photos')) || [];
    let currentPhotoURL = null;

    const modifyPhotoInput = document.getElementById('modifyPhotoInput');
    const modifyPhotoPreview = document.getElementById('modifyPhotoPreview');

    if (signinBtn) {
        signinBtn.addEventListener('click', function(event) {
            event.preventDefault();
            signinModal.style.display = 'block';
        });
    }

    if (signinForm) {
        signinForm.addEventListener('submit', function(event) {
            event.preventDefault();
            const nickname = document.getElementById('signinNickname').value;
            const ID = document.getElementById('signinID').value;
            const password = document.getElementById('signinPassword').value;

            if (localStorage.getItem(ID)) {
                showAlert('This ID is already registered.');
            } else {
                const user = {
                    nickname: nickname,
                    ID: ID,
                    password: password
                };
                localStorage.setItem(ID, JSON.stringify(user));
                showAlert('Registration successful!');
                signinForm.reset();
                signinModal.style.display = 'none';
                if (userListContainer && userList) {
                    loadUserList(currentUser !== null); 
                }
            }
        });
    }

    if (loginBtn) {
        loginBtn.addEventListener('click', function(event) {
            event.preventDefault();
            loginModal.style.display = 'block';
        });
    }

    if (loginForm) {
        loginForm.addEventListener('submit', function(event) {
            event.preventDefault();
            const ID = document.getElementById('loginID').value;
            const password = document.getElementById('loginPassword').value;
            const storedUser = JSON.parse(localStorage.getItem(ID));

            if (storedUser && storedUser.password === password) {
                showAlert('Login successful!');
                loginForm.reset();
                currentUser = storedUser;
                localStorage.setItem('loggedInUser', JSON.stringify(storedUser));
                updateUIAfterLogin(storedUser.nickname);
                if (userListContainer && userList) {
                    loadUserList(true); 
                }
            } else {
                showAlert('Invalid email or password.');
            }
        });
    }

    function updateUIAfterLogin(nickname) {
        if (notLoginedNav) notLoginedNav.style.display = 'none';
        if (loginedNav) loginedNav.style.display = 'flex';
        if (welcomeMessage) welcomeMessage.textContent = `Welcome, ${nickname}`;
        if (nicknameDisplay) nicknameDisplay.textContent = nickname;
        if (loginModal) loginModal.style.display = 'none';
        loadUserMessages(); 
    }

    const loggedInUser = localStorage.getItem('loggedInUser');
    if (loggedInUser) {
        currentUser = JSON.parse(loggedInUser);
        updateUIAfterLogin(currentUser.nickname);
        const savedPhotos = JSON.parse(localStorage.getItem(`${currentUser.ID}_photos`));
        if (savedPhotos) {
            savedPhotos.forEach(photoData => {
                displayUploadedPhoto(photoContainer, photoData, false, true);
            });
        }
        if (userListContainer && userList) {
            loadUserList(true); 
        }
    } else {
        if (userListContainer && userList) {
            loadUserList(false); 
        }
    }

    logoutBtns.forEach(logoutBtn => {
        logoutBtn.addEventListener('click', function(event) {
            event.preventDefault();
            logout();
            window.location.href = '/';
        });
    });

    function logout() {
        localStorage.removeItem('loggedInUser');
        currentUser = null;
        if (notLoginedNav) notLoginedNav.style.display = 'flex';
        if (loginedNav) loginedNav.style.display = 'none';
        if (welcomeMessage) welcomeMessage.textContent = '';
        if (nicknameDisplay) nicknameDisplay.textContent = '';
        if (photoContainer) photoContainer.innerHTML = '';
        if (userListContainer && userList) {
            loadUserList(false); 
        }
    }

    function generateUUID() {
        return 'xxxxxxxx-xxxx-4xxx-yxxx-xxxxxxxxxxxx'.replace(/[xy]/g, function(c) {
            var r = Math.random() * 16 | 0;
            var v = c == 'x' ? r : (r & 0x3 | 0x8);
            return v.toString(16);
        });
    }

    if (uploadBtn) {
        uploadBtn.addEventListener('click', function(event) {
            event.preventDefault();
            if (currentUser) {
                uploadModal.style.display = 'block';
            } else {
                showAlert('Please log in to upload a photo.');
            }
        });
    }

    if (uploadForm) {
        uploadForm.addEventListener('submit', function(event) {
            event.preventDefault();
            const file = photoInput.files[0];
            const description = photoDescription.value;
            const keywords = photoKeywords.value;
            const id = generateUUID(); 
            if (file && currentUser) {
                const reader = new FileReader();
                reader.onload = function(e) {
                    const photoURL = e.target.result;
                    const photoData = {
                        photoURL: photoURL,
                        description: description,
                        keywords: keywords,
                        userID: currentUser.ID,
                        nickname: currentUser.nickname,
                        id: id 
                    };
                    displayUploadedPhoto(photoContainer, photoData, false, true);
                    savePhotoData(photoData);
                    showAlert('Photo uploaded successfully!');
                    uploadModal.style.display = 'none';
                    resetUploadModal();
                };
                reader.readAsDataURL(file);
            }
        });
    }

    if (photoInput) {
        photoInput.addEventListener('change', function(event) {
            const file = event.target.files[0];
            if (file) {
                const reader = new FileReader();
                reader.onload = function(e) {
                    photoPreview.src = e.target.result;
                    photoPreview.style.display = 'block';
                };
                reader.readAsDataURL(file);
            }
        });
    }

    function displayUploadedPhoto(container, photoData, showPoster, isUserPhoto) {
        const { photoURL, description, keywords, nickname, id, userID } = photoData;
        if (container) {
            const photoItem = document.createElement('div');
            photoItem.classList.add('photo-item');
            photoItem.dataset.id = id;
            photoItem.innerHTML = `
                <img src="${photoURL}" alt="Uploaded Photo">
                <p>${description}</p>
                <p><strong>Keywords:</strong> ${keywords}</p>
                ${showPoster ? `<p><strong>Posted by:</strong> ${nickname}</p>` : ''}
                <div class="photo-buttons">
                    ${isUserPhoto ? `
                        <button class="modify-button" onclick="openModifyModal('${id}', '${photoURL}', '${description}', '${keywords}')">Modify</button>
                        <button class="delete-button" onclick="deletePhoto('${id}')">Delete</button>
                    ` : `
                        <button class="message-button" style="display: ${currentUser && currentUser.ID !== userID ? 'inline-block' : 'none'};" onclick="window.location.href='/message?user=${userID}';">Message</button>
                    `}
                </div>
            `;
            container.appendChild(photoItem);
        }
    }

    window.openModifyModal = function(photoID, photoURL, description, keywords) {
        currentPhotoURL = photoURL;
        modifyForm.dataset.id = photoID; 
        modifyDescription.value = description;
        modifyKeywords.value = keywords;
        modifyPhotoPreview.src = photoURL;
        modifyPhotoPreview.style.display = 'block';
        modifyModal.style.display = 'block';
    }

    window.deletePhoto = function(photoID) {
        let photos = JSON.parse(localStorage.getItem('all_photos')) || [];
        photos = photos.filter(photo => photo.id !== photoID);
        localStorage.setItem('all_photos', JSON.stringify(photos));

        if (currentUser) {
            let userPhotos = JSON.parse(localStorage.getItem(`${currentUser.ID}_photos`)) || [];
            userPhotos = userPhotos.filter(photo => photo.id !== photoID);
            localStorage.setItem(`${currentUser.ID}_photos`, JSON.stringify(userPhotos));
        }

        const photoItems = document.querySelectorAll('.photo-item');
        photoItems.forEach(photoItem => {
            if (photoItem.dataset.id === photoID) {
                photoItem.remove();
            }
        });

        showAlert('Photo deleted successfully!');
    }

    function savePhotoData(photoData) {
        let savedPhotos = JSON.parse(localStorage.getItem('all_photos')) || [];
        savedPhotos.push(photoData);
        localStorage.setItem('all_photos', JSON.stringify(savedPhotos));

        let userPhotos = JSON.parse(localStorage.getItem(`${photoData.userID}_photos`)) || [];
        userPhotos.push(photoData);
        localStorage.setItem(`${photoData.userID}_photos`, JSON.stringify(userPhotos));
    }

    if (modifyPhotoInput) {
        modifyPhotoInput.addEventListener('change', function(event) {
            const file = event.target.files[0];
            if (file) {
                const reader = new FileReader();
                reader.onload = function(e) {
                    modifyPhotoPreview.src = e.target.result;
                    modifyPhotoPreview.style.display = 'block';
                };
                reader.readAsDataURL(file);
            }
        });
    }

    function resetUploadModal() {
        if (photoInput) {
            photoInput.value = '';
        }
        if (photoDescription) {
            photoDescription.value = '';
        }
        if (photoKeywords) {
            photoKeywords.value = '';
        }
        if (photoPreview) {
            photoPreview.src = '#';
            photoPreview.style.display = 'none';
        }
    }

    function resetModifyModal() {
        if (modifyDescription) {
            modifyDescription.value = '';
        }
        if (modifyKeywords) {
            modifyKeywords.value = '';
        }
        if (modifyPhotoPreview) {
            modifyPhotoPreview.src = '#';
            modifyPhotoPreview.style.display = 'none';
        }
    }

    if (modifyForm) {
        modifyForm.addEventListener('submit', function(event) {
            event.preventDefault();
            const description = modifyDescription.value;
            const keywords = modifyKeywords.value;
            const photoID = modifyForm.dataset.id; 

            let newPhotoURL = currentPhotoURL;  
            if (modifyPhotoInput.files.length > 0) {
                const file = modifyPhotoInput.files[0];
                const reader = new FileReader();
                reader.onload = function(e) {
                    newPhotoURL = e.target.result;
                    modifyPhoto(photoID, newPhotoURL, description, keywords);
                };
                reader.readAsDataURL(file);
            } else {
                modifyPhoto(photoID, newPhotoURL, description, keywords);
            }
            modifyModal.style.display = 'none';
            resetModifyModal();
        });
    }

    function modifyPhoto(photoID, newPhotoURL, description, keywords) {
        let photos = JSON.parse(localStorage.getItem('all_photos')) || [];
        photos = photos.map(photo => {
            if (photo.id === photoID) {
                photo.photoURL = newPhotoURL;
                photo.description = description;
                photo.keywords = keywords;
            }
            return photo;
        });
        localStorage.setItem('all_photos', JSON.stringify(photos));

        if (currentUser) {
            let userPhotos = JSON.parse(localStorage.getItem(`${currentUser.ID}_photos`)) || [];
            userPhotos = userPhotos.map(photo => {
                if (photo.id === photoID) {
                    photo.photoURL = newPhotoURL;
                    photo.description = description;
                    photo.keywords = keywords;
                }
                return photo;
            });
            localStorage.setItem(`${currentUser.ID}_photos`, JSON.stringify(userPhotos));
        }

        const photoItems = document.querySelectorAll('.photo-item');
        photoItems.forEach(photoItem => {
            if (photoItem.dataset.id === photoID) {
                photoItem.querySelector('img').src = newPhotoURL;
                photoItem.querySelector('p:nth-of-type(1)').textContent = description;
                photoItem.querySelector('p:nth-of-type(2)').innerHTML = `<strong>Keywords:</strong> ${keywords}`;
            }
        });
    }

    if (searchButton) {
        searchButton.addEventListener('click', function(event) {
            event.preventDefault();
            const keyword = searchKeyword.value.trim().toLowerCase();
            filterPhotosByKeyword(keyword);
        });
    }

    function filterPhotosByKeyword(keyword) {
        if (!currentUser) {
            showAlert('Please log in to search photos.');
            return;
        }

        if (searchResults) {
            searchResults.innerHTML = '';
            const savedPhotos = JSON.parse(localStorage.getItem('all_photos')) || [];
            let foundPhotos = false;

            savedPhotos.forEach(photoData => {
                if (photoData.keywords.toLowerCase().includes(keyword)) {
                    displayUploadedPhoto(searchResults, photoData, true, false); // 검색 결과에서는 isUserPhoto를 false로 설정
                    foundPhotos = true;
                }
            });

            if (!foundPhotos) {
                showAlert('No Result');
            } else {
                if (!document.body.contains(searchResults)) {
                    document.body.appendChild(searchResults);
                }
            }
        }
    }

    if (searchKeyword) {
        searchKeyword.addEventListener('keydown', function(event) {
            if (event.key === 'Enter') {
                event.preventDefault();
                searchButton.click();
            }
        });
    }

    if (pageTitle) {
        pageTitle.addEventListener('click', function(event) {
            event.preventDefault();
            window.location.href = '/';
        });
    }

    if (homeIcon) {
        homeIcon.addEventListener('click', function(event) {
            event.preventDefault();
            const loggedInUser = localStorage.getItem('loggedInUser');
            if (loggedInUser) {
                window.location.href = '/personal_page';
            } else {
                showAlert('Please Login');
            }
        });
    }

    if (closeLogin) {
        closeLogin.addEventListener('click', function() {
            loginModal.style.display = 'none';
        });
    }

    if (closeSignin) {
        closeSignin.addEventListener('click', function() {
            signinModal.style.display = 'none';
        });
    }

    if (closeUpload) {
        closeUpload.addEventListener('click', function() {
            uploadModal.style.display = 'none';
            resetUploadModal();
        });
    }

    if (closeAlert) {
        closeAlert.addEventListener('click', function() {
            alertBox.style.display = 'none';
        });
    }

    if (closeImageModal) {
        closeImageModal.addEventListener('click', function() {
            imageModal.style.display = 'none';
        });
    }

    if (closeModify) {
        closeModify.addEventListener('click', function() {
            modifyModal.style.display = 'none';
        });
    }

    window.addEventListener('click', function(event) {
        if (event.target === loginModal) {
            loginModal.style.display = 'none';
        }
        if (event.target === signinModal) {
            signinModal.style.display = 'none';
        }
        if (event.target === imageModal) {
            imageModal.style.display = 'none';
        }
        if (event.target === modifyModal) {
            modifyModal.style.display = 'none';
        }
    });

    function showAlert(message) {
        if (alertMessage) alertMessage.textContent = message;
        if (alertBox) alertBox.style.display = 'block';
    }

    function loadUserList(showMessageButton = false) {
        if (!userList) {
            console.error('User list element not found');
            return;
        }

        userList.innerHTML = ''; 

        for (let i = 0; i < localStorage.length; i++) {
            const key = localStorage.key(i);
            if (key && key !== 'loggedInUser' && key !== 'all_photos' && key !== 'messages') {
                const user = JSON.parse(localStorage.getItem(key));
                if (user && user.nickname && user.ID) {
                    const listItem = document.createElement('li');
                    listItem.innerHTML = `Nickname: ${user.nickname}, ID: ${user.ID}`;
                    if (showMessageButton && user.ID !== currentUser.ID) {
                        const messageButton = document.createElement('button');
                        messageButton.classList.add('message-button');
                        messageButton.textContent = 'Message';
                        messageButton.onclick = function() {
                            window.location.href = `/message?user=${user.ID}`; 
                        };
                        listItem.appendChild(messageButton);
                    }
                    userList.appendChild(listItem);
                }
            }
        }
    }

    document.body.appendChild(searchResults);
    
    
    function loadUserMessages() {
        const loggedInUser = JSON.parse(localStorage.getItem('loggedInUser'));
        if (!loggedInUser) {
            return;
        }

        const messages = JSON.parse(localStorage.getItem('messages')) || {};
        const userMessages = [];

        for (const key in messages) {
            if (key.includes(loggedInUser.ID)) {
                userMessages.push(...Object.values(messages[key]).filter(message => message.recipient === loggedInUser.ID));
            }
        }

        displayUserMessages(userMessages);
    }

    
    function displayUserMessages(messages) {
        const messageListContainer = document.getElementById('messageListContainer');
        if (!messageListContainer) {
            return;
        }

        messageListContainer.innerHTML = '';

        
        messages.sort((a, b) => new Date(b.timestamp) - new Date(a.timestamp)).forEach(message => {
            const messageItem = document.createElement('div');
            messageItem.classList.add('message-item');
            messageItem.innerHTML = `
                <p><strong>From:</strong> ${message.sender}</p>
                <p>${message.text}</p>
                <p><small>${message.timestamp}</small></p>
            `;
            messageItem.addEventListener('click', () => {
                window.location.href = `/message?user=${message.sender}`;
            });
            messageListContainer.prepend(messageItem);
        });
    }

    
    if (loggedInUser) {
        loadUserMessages();
    }
});
