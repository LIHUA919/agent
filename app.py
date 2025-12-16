from flask import Flask, render_template, request, jsonify
import openai

# 设置OpenAI API密钥
openai.api_key = 'your-openai-api-key'

app = Flask(__name__)

# 使用OpenAI API生成回复
def generate_response(user_input):
    response = openai.Completion.create(
        engine="text-davinci-003",  # 使用GPT-3.5模型
        prompt=user_input,
        max_tokens=150
    )
    return response.choices[0].text.strip()

# 主页路由
@app.route('/')
def index():
    return render_template('index.html')

# 处理对话请求的路由
@app.route('/chat', methods=['POST'])
def chat():
    user_input = request.form['user_input']
    response = generate_response(user_input)
    return jsonify({'response': response})

if __name__ == '__main__':
    app.run(debug=True)