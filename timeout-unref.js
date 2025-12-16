


console.log(1)

var x = setTimeout(() => {
  console.log(2)
}, 5000)

x.unref()
x.ref()
