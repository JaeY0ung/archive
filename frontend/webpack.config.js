const webpack = require('webpack')

module.exports = {
  // ... 다른 설정들
    plugins: [
        new webpack.DefinePlugin({
            __VUE_PROD_HYDRATION_MISMATCH_DETAILS__: JSON.stringify(false)
        })
    ]
}