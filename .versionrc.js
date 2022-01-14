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
      filename: "./deploy/charts/prox-professor-profile-service/Chart.yaml",
      updater:
        "./node_modules/@map-colonies/standard-version-update-helm-version/src/index.js",
    },
    {
      filename: "package.json",
      type: "json",
    },
  ],
};
