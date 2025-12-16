
// const fs = require('fs')

// fs.readFile('woiejf', (err, data) => {
//   setTimeout(() => {
//     console.log(1) 
//   })
//   setImmediate(() => {
//     console.log(2) 
//   })
//   process.nextTick(() => {
//     console.log(3)
//   })
// })


// setTimeout(() => {
//   console.log(1)
//   process.nextTick(() => {
//     console.log('tick1')
//   })
//   process.nextTick(() => {
//     console.log('tick2')
//     process.nextTick(() => {
//       console.log('tick2.1')
//     })
//   })
//   process.nextTick(() => {
//     console.log('tick3')
//   })
// })
// setTimeout(() => {
//   console.log(2)
// })


// setImmediate(() => {
//   console.log('immediate')
// })

// var count = 0
// var start = Date.now()
// setTimeout(function f() {
//   count++
//   if (Date.now() - start < 1000) {
//     setTimeout(f)
//   } else {
//     console.log(count)
//   }
// })

// Promise.resolve().then(() => {
//   console.log(1)
// })

// process.nextTick(() => {
//   console.log(2)
// })

setImmediate(() => {
  console.log('imm')
})

Promise.resolve().then(function f() {
  process.nextTick(() => {console.log(1)})
  Promise.resolve().then(f)
})