/** @type {import('orval').Config} */
module.exports = {
  linguaflow: {
    input: '../Server/rest-api-module/src/main/resources/static/openapi.yaml',
    output: {
      target: './src/generatedAPI',
      schemas: './src/generatedAPI/model',
      client: 'fetch',
      mode: 'tags-split',
      clean: true,
      baseUrl: 'http://localhost:8080',

      override: {
        requestOptions: {
          credentials: 'include',
        },
      },
    },
  },
};
