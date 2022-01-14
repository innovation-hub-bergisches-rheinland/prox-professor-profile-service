module.exports = {
  bumpFiles: [
    /*{
      filename: "pom.xml",
      updater: "./node_modules/standard-version-maven/index.js",
    },*/
    {
      filename: "version.txt",
      type: "plain-text",
    },
    {
      filename: "package.json",
      type: "json",
    },
  ],
};
