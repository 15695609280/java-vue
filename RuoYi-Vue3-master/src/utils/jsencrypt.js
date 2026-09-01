import JSEncrypt from 'jsencrypt/bin/jsencrypt.min'

// RSA 公私钥从环境变量读取，避免硬编码在前端源码中
// 配置位置：项目根目录 .env.development / .env.production / .env.staging 中的 VITE_RSA_PUBLIC_KEY、VITE_RSA_PRIVATE_KEY
// 密钥对生成 http://web.chacuo.net/netrsakeypair
// 环境变量里的 \n 是换行符的转义写法（.env 文件不支持多行值），这里还原成真实换行
const publicKey = (import.meta.env.VITE_RSA_PUBLIC_KEY || '').replace(/\\n/g, '\n')
const privateKey = (import.meta.env.VITE_RSA_PRIVATE_KEY || '').replace(/\\n/g, '\n')

// 加密
export function encrypt(txt) {
  const encryptor = new JSEncrypt()
  encryptor.setPublicKey(publicKey) // 设置公钥
  return encryptor.encrypt(txt) // 对数据进行加密
}

// 解密
export function decrypt(txt) {
  const encryptor = new JSEncrypt()
  encryptor.setPrivateKey(privateKey) // 设置私钥
  return encryptor.decrypt(txt) // 对数据进行解密
}
