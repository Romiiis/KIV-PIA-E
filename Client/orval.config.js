/** @type {import('orval').Config} */
module.exports = {
  api: {
    input: '../openapi.yaml',
    output: {
      target: './src/generatedAPI/index.ts',
      schemas: './src/generatedAPI/models',
      client: 'axios',
      mode: 'tags-split',
      override: {
        mutator: {
          path: './src/app/api/axios-instance.ts',
          name: 'axiosInstance',
        },
        operations: {
          // oba downloady mají octet-stream => blob
          downloadOriginalContent: {
            responseType: 'blob',
          },
          downloadTranslatedContent: {
            responseType: 'blob',
          },
          // uploadTranslatedContent má multipart/form-data – Orval to zvládne sám
        },
      },
    },
  },
};
