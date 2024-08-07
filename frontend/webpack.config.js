const path = require('path');
const webpack = require('webpack');
const Dotenv = require('dotenv-webpack');
const CopyWebpackPlugin = require('copy-webpack-plugin');
const fs = require('fs');

require('dotenv').config();

const swTemplatePath = path.resolve(__dirname, 'public/firebase-messaging-sw.template.js');
const swOutputPath = path.resolve(__dirname, 'public/firebase-messaging-sw.js');

const swTemplateContent = fs.readFileSync(swTemplatePath, 'utf8');

const swContent = swTemplateContent
  .replace(/VUE_APP_FIREBASE_API_KEY/g, process.env.VUE_APP_FIREBASE_API_KEY)
  .replace(/VUE_APP_FIREBASE_AUTH_DOMAIN/g, process.env.VUE_APP_FIREBASE_AUTH_DOMAIN)
  .replace(/VUE_APP_FIREBASE_PROJECT_ID/g, process.env.VUE_APP_FIREBASE_PROJECT_ID)
  .replace(/VUE_APP_FIREBASE_STORAGE_BUCKET/g, process.env.VUE_APP_FIREBASE_STORAGE_BUCKET)
  .replace(/VUE_APP_FIREBASE_MESSAGING_SENDER_ID/g, process.env.VUE_APP_FIREBASE_MESSAGING_SENDER_ID)
  .replace(/VUE_APP_FIREBASE_APP_ID/g, process.env.VUE_APP_FIREBASE_APP_ID)
  .replace(/VUE_APP_FIREBASE_MEASUREMENT_ID/g, process.env.VUE_APP_FIREBASE_MEASUREMENT_ID);

fs.writeFileSync(swOutputPath, swContent, 'utf8');

module.exports = {
  entry: {
    main: './src/main.js'
  },
  output: {
    path: path.resolve(__dirname, 'dist'),
    filename: '[name].bundle.js' // 이름에 따라 번들링된 파일 명 설정
  },
  resolve: {
    alias: {
      '@': path.resolve(__dirname, 'src')
    }
  },
  module: {
    rules: [
      {
        test: /\.js$/,
        include: [
          path.resolve(__dirname, 'src'),
          path.resolve(__dirname, 'src/assets/js/opensheetmusicdisplay.min.js')
        ],
        use: {
          loader: 'babel-loader',
          options: {
            presets: ['@babel/preset-env'],
            plugins: ['@babel/plugin-proposal-class-properties', '@babel/plugin-transform-runtime']
          }
        }
      },
      {
        test: /\.vue$/,
        loader: 'vue-loader'
      },
      {
        test: /\.css$/,
        use: [
          'vue-style-loader',
          'css-loader'
        ]
      },
      {
        test: /\.(png|jpg|gif|svg)$/, // 모든 이미지 파일 처리
        use: [
          {
            loader: 'file-loader',
            options: {
              name: '[name].[ext]?[hash]'
            }
          }
        ]
      },
      {
        test: /\.svg$/, // SVG 파일 처리
        use: 'vue-svg-loader'
      }
    ]
  },
  plugins: [
    new webpack.DefinePlugin({
      __VUE_PROD_HYDRATION_MISMATCH_DETAILS__: JSON.stringify(false),
      'process.env.VUE_APP_FIREBASE_API_KEY': JSON.stringify(process.env.VUE_APP_FIREBASE_API_KEY),
      'process.env.VUE_APP_FIREBASE_AUTH_DOMAIN': JSON.stringify(process.env.VUE_APP_FIREBASE_AUTH_DOMAIN),
      'process.env.VUE_APP_FIREBASE_PROJECT_ID': JSON.stringify(process.env.VUE_APP_FIREBASE_PROJECT_ID),
      'process.env.VUE_APP_FIREBASE_STORAGE_BUCKET': JSON.stringify(process.env.VUE_APP_FIREBASE_STORAGE_BUCKET),
      'process.env.VUE_APP_FIREBASE_MESSAGING_SENDER_ID': JSON.stringify(process.env.VUE_APP_FIREBASE_MESSAGING_SENDER_ID),
      'process.env.VUE_APP_FIREBASE_APP_ID': JSON.stringify(process.env.VUE_APP_FIREBASE_APP_ID),
      'process.env.VUE_APP_FIREBASE_MEASUREMENT_ID': JSON.stringify(process.env.VUE_APP_FIREBASE_MEASUREMENT_ID)
    }),
    new Dotenv(),
    new CopyWebpackPlugin({
      patterns: [
        { from: 'public', to: '' }
      ]
    })
  ]
};
