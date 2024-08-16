config.resolve = {
    fallback: {
        fs: false,
        path: false,
    //    crypto: false,
    //    os: require.resolve("os-browserify/browser")
    }
};

const CopyWebpackPlugin = require('copy-webpack-plugin');
config.plugins.push(
    new CopyWebpackPlugin({
        patterns: [
            '../../node_modules/sql.js/dist/sql-wasm.wasm'
        ]
    })
);