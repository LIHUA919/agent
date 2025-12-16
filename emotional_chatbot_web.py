import random
import re
import nltk
import torch
from transformers import AutoTokenizer, AutoModelForCausalLM
from nltk.sentiment import SentimentIntensityAnalyzer
from flask import Flask, render_template, request, jsonify

# 下载 NLTK 的情绪分析器
nltk.download('vader_lexicon')
sia = SentimentIntensityAnalyzer()

# 初始化 Flask 应用
app = Flask(__name__)

# 初始化 NLP 模型
tokenizer = AutoTokenizer.from_pretrained("microsoft/DialoGPT-medium")
model = AutoModelForCausalLM.from_pretrained("microsoft/DialoGPT-medium")

greetings = [
    "Hello! How can I assist you today?",
    "Hi there! What's on your mind?",
    "Hey! I'm here to talk if you need me."
]
goodbye = [
    "Goodbye! Take care!",
    "See you next time. Remember, I'm always here to chat!",
    "Bye! Stay positive!"
]
positive_responses = [
    "That's wonderful! I'm so happy to hear that!",
    "Yay! It sounds like things are going well!",
    "Great! Keep up the good vibes!"
]
negative_responses = [
    "I'm really sorry you're feeling this way. Want to talk more about it?",
    "That sounds tough. I'm here for you if you need to talk.",
    "I understand, sometimes things can be really hard. How can I help?"
]
neutral_responses = [
    "I see, can you tell me more about it?",
    "Hmm, that sounds interesting. What else?",
    "I understand. How does that make you feel?"
]

def analyze_sentiment(message):
    # 使用情绪分析器来分析用户输入的情绪
    sentiment = sia.polarity_scores(message)
    if sentiment['compound'] >= 0.5:
        return 'positive'
    elif sentiment['compound'] <= -0.5:
        return 'negative'
    else:
        return 'neutral'

def generate_response(message):
    # 处理打招呼或告别的特殊情况
    if re.search(r'\bhello\b|\bhi\b|\bhey\b', message, re.IGNORECASE):
        return random.choice(greetings)
    elif re.search(r'\bbye\b|\bgoodbye\b|\bsee you\b', message, re.IGNORECASE):
        return random.choice(goodbye)
    
    # 分析情绪并生成相应的回答
    sentiment = analyze_sentiment(message)
    if sentiment == 'positive':
        return random.choice(positive_responses)
    elif sentiment == 'negative':
        return random.choice(negative_responses)
    else:
        # 使用 NLP 模型生成回答
        inputs = tokenizer.encode(message + tokenizer.eos_token, return_tensors='pt')
        reply_ids = model.generate(inputs, max_length=100, pad_token_id=tokenizer.eos_token_id)
        response = tokenizer.decode(reply_ids[:, inputs.shape[-1]:][0], skip_special_tokens=True)
        return response

@app.route('/')
def home():
    return render_template('chat.html')

@app.route('/get_response', methods=['POST'])
def get_response():
    user_message = request.form['message']
    response = generate_response(user_message)
    return jsonify({'response': response})

if __name__ == "__main__":
    app.run(debug=True)

# chat.html (用于显示漂亮的网页)
html_content = """
<!DOCTYPE html>
<html lang="en">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>Emotional ChatBot</title>
    <link rel="stylesheet" href="https://cdnjs.cloudflare.com/ajax/libs/bulma/0.9.3/css/bulma.min.css">
    <style>
        .chat-container {
            max-width: 600px;
            margin: 50px auto;
            padding: 20px;
            border-radius: 10px;
            background-color: #f7f7f7;
            box-shadow: 0 0 10px rgba(0, 0, 0, 0.1);
        }
        .chat-box {
            max-height: 400px;
            overflow-y: auto;
            margin-bottom: 20px;
            padding: 10px;
            border: 1px solid #ccc;
            border-radius: 10px;
            background-color: #fff;
        }
        .user-message {
            text-align: right;
            color: blue;
            margin: 5px;
        }
        .bot-message {
            text-align: left;
            color: green;
            margin: 5px;
        }
    </style>
</head>
<body>
    <div class="chat-container box">
        <h1 class="title has-text-centered">Emotional ChatBot</h1>
        <div id="chat-box" class="chat-box"></div>
        <div class="field has-addons">
            <div class="control is-expanded">
                <input id="user-input" class="input" type="text" placeholder="Type your message...">
            </div>
            <div class="control">
                <button id="send-button" class="button is-primary">Send</button>
            </div>
        </div>
    </div>
    <script>
        document.getElementById('send-button').addEventListener('click', function() {
            const userInput = document.getElementById('user-input').value;
            if (userInput.trim() === '') return;

            const chatBox = document.getElementById('chat-box');
            const userMessage = document.createElement('div');
            userMessage.className = 'user-message';
            userMessage.textContent = 'You: ' + userInput;
            chatBox.appendChild(userMessage);

            fetch('/get_response', {
                method: 'POST',
                headers: {
                    'Content-Type': 'application/x-www-form-urlencoded',
                },
                body: new URLSearchParams({ 'message': userInput })
            })
            .then(response => response.json())
            .then(data => {
                const botMessage = document.createElement('div');
                botMessage.className = 'bot-message';
                botMessage.textContent = 'Bot: ' + data.response;
                chatBox.appendChild(botMessage);
                chatBox.scrollTop = chatBox.scrollHeight;
            });

            document.getElementById('user-input').value = '';
        });
    </script>
</body>
</html>
"""

# 将 HTML 内容写入文件
with open('templates/chat.html', 'w') as file:
    file.write(html_content)
