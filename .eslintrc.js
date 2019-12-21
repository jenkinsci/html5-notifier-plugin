/* eslint-env: node */
module.exports = {
  'env': {
    'browser': true,
    'es6': true
  },
  'extends': [
    'eslint:recommended',
    'plugin:import/recommended',
  ],
  'parser': 'babel-eslint',
  'plugins': [
    'import'
  ],
  'rules': {
    'eol-last': 2,
    'import/no-unresolved': [2],
    'indent': ['error', 2],
    'key-spacing': [2],
    'max-len': 0,
    'no-console': 0,
    'no-extra-semi': 2,
    'no-multi-spaces': 'error',
    'no-trailing-spaces': [0, {'skipBlankLines': true}],
    'no-undef': 2,
    'no-underscore-dangle': [0],
    'no-unused-vars': [2],
    'no-var': 2,
    'object-curly-spacing': ['error', 'never'],
    'object-shorthand': [0, 'always'],
    'prefer-arrow-callback': 2,
    'prefer-const': 2,
    'prefer-template': 2,
    'quote-props': [0, 'as-needed'],
    'quotes': [2, 'single'],
    'semi': [2],
    'space-before-function-paren': [2, {'anonymous': 'always','named': 'never'}],
    'strict': [2, 'global'],
  }
};
