module.exports = {
  // Delegate the whole formatting to spotless
  "*": [
    (resolvedPaths) => {
      return `./mvnw spotless:apply -X -DspotlessFiles=${resolvedPaths.join(
        ","
      )}`;
    },
  ],
};
