import {GeneratorConfig} from 'ng-openapi';

const config: GeneratorConfig = {
  input: '../Server/rest-api-module/src/main/resources/static/openapi.yaml',
  output: './src/generatedAPI',
  clientName: "LinguaFlowClient",
  options: {
    dateType: 'Date',
    enumStyle: 'enum',
  },
};
export default config;
