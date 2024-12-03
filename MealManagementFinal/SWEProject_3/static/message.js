// 메시지를 저장할 배열
let messages = {};
let currentUser = null;
let recipientUser = null;

// 로컬 스토리지 키
const STORAGE_KEY = 'messages';
const urlParams = new URLSearchParams(window.location.search);
const recipient = urlParams.get('user');

// 로컬 스토리지에서 메시지 로드
function loadMessages() {
    const storedMessages = localStorage.getItem(STORAGE_KEY);
    if (storedMessages) {
        messages = JSON.parse(storedMessages);
    } else {
        messages = {};
    }
}

// 메시지 저장
function saveMessages() {
    localStorage.setItem(STORAGE_KEY, JSON.stringify(messages));
}


function getMessageKey(user1, user2) {
    return [user1, user2].sort().join('_');
}


function displayUserInfo() {
    const userInfoDiv = document.getElementById('userInfo');
    if (currentUser && recipientUser) {
        userInfoDiv.textContent = `${recipientUser.nickname}(${recipientUser.ID})`;
    }
}

// 초기 메시지 로드
window.onload = function() {
    const loggedInUser = localStorage.getItem('loggedInUser');
    const storedRecipientUser = localStorage.getItem(recipient);
    if (loggedInUser) {
        currentUser = JSON.parse(loggedInUser);
        if (storedRecipientUser) {
            recipientUser = JSON.parse(storedRecipientUser);
        }
        loadMessages();
        displayUserInfo();
        renderMessages();
    } else {
        alert("You need to log in first.");
        window.location.href = '/';
    }
}

// 메시지 전송
function sendMessage() {
    const messageInput = document.getElementById('messageInput');
    const messageText = messageInput.value.trim();

    if (messageText !== '') {
        const newMessage = {
            id: Date.now(),
            text: messageText,
            timestamp: new Date().toLocaleString(),
            sender: currentUser.ID,
            recipient: recipient
        };

        const messageKey = getMessageKey(currentUser.ID, recipient);

        if (!messages[messageKey]) {
            messages[messageKey] = [];
        }

        
        messages[messageKey].push(newMessage);

        
        saveMessages();

        
        renderMessages();

        
        messageInput.value = '';
    }
}

// 메시지 출력
function renderMessages() {
    const messageContainer = document.getElementById('messageContainer');
    messageContainer.innerHTML = '';

    const messageKey = getMessageKey(currentUser.ID, recipient);

    if (messages[messageKey]) {
        messages[messageKey].forEach(message => {
            const messageDiv = document.createElement('div');
            messageDiv.className = 'message';
            if (message.sender !== currentUser.ID) {
                messageDiv.classList.add('message-received');
                messageDiv.innerHTML = `
                    <p>${message.text}</p>
                    <span class="timestamp">${message.timestamp}</span>
                    <div class="message-actions">
                        <button onclick="replyMessage(${message.id})">Reply</button>
                    </div>
                `;
            } else {
                messageDiv.classList.add('message-sent');
                messageDiv.innerHTML = `
                    <p>${message.text}</p>
                    <span class="timestamp">${message.timestamp}</span>
                    <div class="message-actions">
                        <button onclick="deleteMessage(${message.id})">Delete</button>
                    </div>
                `;
            }

            if (message.replyTo) {
                const originalMessage = messages[messageKey].find(msg => msg.id === message.replyTo);
                if (originalMessage) {
                    const replyInfo = document.createElement('div');
                    replyInfo.className = 'reply-info';
                    replyInfo.innerHTML = `
                        <p class="original-message"><strong>Reply to:</strong> ${originalMessage.text}</p>
                    `;
                    messageDiv.insertBefore(replyInfo, messageDiv.firstChild);
                }
            }

            messageContainer.appendChild(messageDiv);
        });
    }

    scrollToBottom();
}

// 최신 메시지로 스크롤
function scrollToBottom() {
    const messageContainer = document.getElementById('messageContainer');
    messageContainer.scrollTop = messageContainer.scrollHeight;
}

// 메시지 삭제
function deleteMessage(id) {
    const messageKey = getMessageKey(currentUser.ID, recipient);
    messages[messageKey] = messages[messageKey].filter(message => message.id !== id);
    saveMessages();
    renderMessages();
}

// 답장 함수
function replyMessage(id) {
    const messageKey = getMessageKey(currentUser.ID, recipient);
    const originalMessage = messages[messageKey].find(message => message.id === id);
    if (originalMessage) {
        const replyText = prompt('Reply to message:', '');
        if (replyText) {
            const replyMessage = {
                id: Date.now(),
                text: replyText,
                timestamp: new Date().toLocaleString(),
                sender: currentUser.ID,
                recipient: recipient,
                replyTo: originalMessage.id
            };
            messages[messageKey].push(replyMessage);
            saveMessages();
            renderMessages();
        }
    }
}

document.addEventListener('DOMContentLoaded', () => {
    const messageInput = document.getElementById('messageInput');
    const sendMessageBtn = document.getElementById('sendMessageBtn');

    
    messageInput.addEventListener('keydown', (event) => {
        if (event.key === 'Enter') {
            event.preventDefault(); 
            sendMessage(); 
        }
    });

    sendMessageBtn.addEventListener('click', sendMessage);

    
    const backButton = document.querySelector('.back-button');
    if (backButton) {
        backButton.addEventListener('click', () => {
            window.history.back();
        });
    }
});
