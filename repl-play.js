const repl = require('node:repl')


var messages = []


repl.start({
  prompt: 'Me> ',
  eval: async function(cmd, context, file, cb) {
    messages.push({ "role": "user", "content": cmd })

    var aiAnswer = await fetch('https://api.openai-proxy.org/v1/chat/completions', {
      method: 'POST',
      headers: {
        authorization: 'Bearer sk-e6YD5VRhPSxY0zEFSQoX12taNXIUTcYV9TYeR36d6z0JpZxF',
        'content-type': 'application/json',
      },
      body: JSON.stringify({
        "messages":
          [
            {
              "role": "system",
              "content": "\nYou are ChatGPT, a large language model trained by OpenAI.\nKnowledge cutoff: 2021-09\nCurrent model: gpt-3.5-turbo\nCurrent time: Sun Nov 17 2024 21:47:58 GMT+0800 (中国标准时间)\nLatex inline: \\(x^2\\) \nLatex block: $$e=mc^2$$\n\n",
            },
            ...messages
          ],
        // "stream": true,
        "model": "gpt-3.5-turbo",
        "temperature": 0.5,
        "presence_penalty": 0,
        "frequency_penalty": 0,
        "top_p": 1,
      }
      )
    }).then(it => it.json())

    messages.push({"role": "assistant", "content": aiAnswer.choices[0].message.content})

    cb(null, aiAnswer.choices[0].message.content)
  },
  writer(answer) {
    return 'AI> ' + answer
  }
})