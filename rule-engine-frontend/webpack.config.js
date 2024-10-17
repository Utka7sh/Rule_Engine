const webpack = require('webpack');

module.exports = {
    resolve: {
        fallback: {
            crypto: require.resolve('crypto-browserify')
        }
    },
    plugins: [
        new webpack.ProvidePlugin({
            process: 'process/browser'
        }),
        new webpack.DefinePlugin({
            'process.env.NODE_ENV': JSON.stringify(process.env.NODE_ENV)
        })
    ]
};
