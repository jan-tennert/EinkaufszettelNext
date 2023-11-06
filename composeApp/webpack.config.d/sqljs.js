const ASSET_PATH = process.env.ASSET_PATH || '/';

config.resolve = {
    fallback: {
        fs: false,
        path: false,
        crypto: false,
        os: false
    }
};

config.output.publicPath = "auto";

const CopyWebpackPlugin = require('copy-webpack-plugin');
config.plugins.push(
    new CopyWebpackPlugin({
        patterns: [
            '../../node_modules/sql.js/dist/sql-wasm.wasm'
        ]
    })
);