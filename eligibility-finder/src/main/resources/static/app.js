let latestMatchedSchemes = [];

document.getElementById('eligibilityForm').addEventListener('submit', async function (e) {
    e.preventDefault();

    const payload = {
        age: parseInt(document.getElementById('age').value, 10),
        gender: document.getElementById('gender').value,
        annualIncome: parseFloat(document.getElementById('annualIncome').value),
        occupation: document.getElementById('occupation').value,
        community: document.getElementById('community').value,
        educationLevel: document.getElementById('educationLevel').value,
        maritalStatus: document.getElementById('maritalStatus').value
    };

    const resultsTitle = document.getElementById('resultsTitle');
    const resultsContainer = document.getElementById('resultsContainer');
    resultsTitle.style.display = 'block';
    resultsTitle.textContent = 'Checking eligibility rules...';
    resultsContainer.innerHTML = '';

    try {
        const response = await fetch('/api/schemes/check', {
            method: 'POST',
            headers: { 'Content-Type': 'application/json' },
            body: JSON.stringify(payload)
        });

        const data = await response.json();

        if (!response.ok) {
            resultsTitle.textContent = `Backend Error (Status ${response.status})`;
            resultsContainer.innerHTML = `<p>${data.error || 'Server error'}: ${data.message || 'Check IntelliJ console.'}</p>`;
            return;
        }

        const schemes = Array.isArray(data) ? data : (data.eligibleSchemes || []);
        latestMatchedSchemes = schemes.map(s => {
            const docs = (s.requiredDocuments || []).map(d => d.documentName).join(', ');
            return `${s.name} (Benefit: ${s.benefitAmount} | Documents: ${docs})`;
        });

        resultsTitle.textContent = `Eligible Schemes Found: ${schemes.length}`;

        if (schemes.length === 0) {
            resultsContainer.innerHTML = '<p>No matching schemes found for the entered profile.</p>';
            return;
        }

        schemes.forEach(scheme => {
            const docsHtml = (scheme.requiredDocuments || [])
                .map(doc => `<li>${doc.documentName}</li>`)
                .join('');

            const card = document.createElement('div');
            card.className = 'scheme-card';
            card.innerHTML = `
                <div class="badges">
                    <span class="badge">${scheme.level}</span>
                    <span class="badge">${scheme.category}</span>
                </div>
                <h3>${scheme.name}</h3>
                <p class="benefit">Benefit: ${scheme.benefitAmount}</p>
                <p>${scheme.description}</p>
                <strong>Required Documents Checklist:</strong>
                <ul>${docsHtml}</ul>
                <div class="card-actions">
                    <a class="apply-btn" href="${scheme.officialUrl}" target="_blank" rel="noopener">Visit Official Portal</a>
                    <button type="button" class="translate-card-btn">Translate &amp; Explain</button>
                </div>
            `;

            card.querySelector('.translate-card-btn').addEventListener('click', () => {
                openChatAndSend(`Please translate "${scheme.name}" (its benefit, description, and required documents) and explain how to apply.`);
            });

            resultsContainer.appendChild(card);
        });
    } catch (error) {
        resultsTitle.textContent = 'Error connecting to backend API';
        resultsContainer.innerHTML = `<p>${error.message}</p>`;
    }
});

// Chatbot & Translation logic
const chatWindow = document.getElementById('chatWindow');
const chatMessages = document.getElementById('chatMessages');

document.getElementById('chatToggleBtn').addEventListener('click', () => {
    chatWindow.style.display = chatWindow.style.display === 'flex' ? 'none' : 'flex';
});

document.getElementById('chatCloseBtn').addEventListener('click', () => {
    chatWindow.style.display = 'none';
});

function appendMessage(text, senderClass) {
    const div = document.createElement('div');
    div.className = `msg ${senderClass}`;
    div.textContent = text;
    chatMessages.appendChild(div);
    chatMessages.scrollTop = chatMessages.scrollHeight;
    return div;
}

async function sendChatPrompt(promptText) {
    const targetLanguage = document.getElementById('targetLanguage').value;
    appendMessage(`[${targetLanguage}] ${promptText}`, 'msg-user');
    const loadingMsg = appendMessage(`Translating & thinking in ${targetLanguage}...`, 'msg-bot');

    try {
        const res = await fetch('/api/chat', {
            method: 'POST',
            headers: { 'Content-Type': 'application/json' },
            body: JSON.stringify({
                message: promptText,
                targetLanguage: targetLanguage,
                matchedSchemes: latestMatchedSchemes
            })
        });
        const data = await res.json();
        loadingMsg.textContent = data.reply || 'No response received.';
    } catch (err) {
        loadingMsg.textContent = 'Error connecting to AI assistant.';
    }
}

function openChatAndSend(promptText) {
    chatWindow.style.display = 'flex';
    sendChatPrompt(promptText);
}

document.getElementById('translateAllBtn').addEventListener('click', () => {
    if (latestMatchedSchemes.length === 0) {
        appendMessage('Please submit the eligibility form first so I can translate your matched schemes!', 'msg-bot');
        return;
    }
    sendChatPrompt('Translate all my matched eligible schemes, their benefits, and required documents.');
});

document.getElementById('chatForm').addEventListener('submit', function (e) {
    e.preventDefault();
    const input = document.getElementById('chatInput');
    const question = input.value.trim();
    if (!question) return;
    input.value = '';
    sendChatPrompt(question);
});