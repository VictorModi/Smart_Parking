const path = require('path');
const webpack = require('webpack');

module.exports = {
    entry: './src/main/web/WEB-INF/assets/js/main.js',
    output: {
        path: path.resolve(__dirname, 'src/main/web/dist'),
        filename: 'bundle.js',
    },
    module: {
        rules: [
            {
                test: /\.css$/,
                use: ['style-loader', 'css-loader'],
            },
            {
                test: /\.(png|svg|jpg|gif)$/,
                use: ['file-loader'],
            },
            {
                test: /\.wasm$/,
                loader: 'base64-loader',
                type: 'javascript/auto',
            },
        ],
    },
    externals: {
        'crypto': "crypto",
    },
    plugins: [
    ],
    resolve: {
        fallback: {
            path: false,
            fs: false,
            Buffer: false,
            process: false,
        },
    },
    mode: 'production',
};
