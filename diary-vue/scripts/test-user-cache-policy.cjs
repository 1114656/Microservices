const assert = require('node:assert/strict')
const fs = require('node:fs')
const path = require('node:path')
const vm = require('node:vm')
const ts = require('typescript')

const sourcePath = path.resolve(__dirname, '../src/store/modules/userCachePolicy.ts')
const source = fs.readFileSync(sourcePath, 'utf8')
const output = ts.transpileModule(source, {
  compilerOptions: {
    module: ts.ModuleKind.CommonJS,
    target: ts.ScriptTarget.ES2020
  }
}).outputText

const sandbox = {
  exports: {},
  module: { exports: {} }
}
sandbox.exports = sandbox.module.exports
vm.runInNewContext(output, sandbox, { filename: sourcePath })

const { USER_PERMISSION_CACHE_SECONDS, hasUsablePermissionCache } = sandbox.module.exports

assert.equal(USER_PERMISSION_CACHE_SECONDS, 30 * 60)

const cachedUser = {
  permissions: ['system:user:query'],
  roles: ['admin'],
  user: { id: 1, nickname: 'admin' }
}

assert.equal(hasUsablePermissionCache(cachedUser, []), true)
assert.equal(hasUsablePermissionCache(cachedUser, [{ path: '/system' }]), true)
assert.equal(hasUsablePermissionCache(null, []), false)
assert.equal(hasUsablePermissionCache(cachedUser, null), false)
assert.equal(hasUsablePermissionCache({ ...cachedUser, permissions: null }, []), false)
assert.equal(hasUsablePermissionCache({ ...cachedUser, roles: null }, []), false)
assert.equal(hasUsablePermissionCache({ ...cachedUser, user: null }, []), false)

console.log('user cache policy tests passed')
