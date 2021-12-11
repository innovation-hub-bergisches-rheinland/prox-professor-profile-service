module.exports = {
  bumpFiles: [
    {
      filename: 'pom.xml',
      updater: './node_modules/standard-version-maven/index.js'
    },
    {
      filename: 'package.json',
      type: 'json'
    }
  ]
};
