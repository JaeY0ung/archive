import {localAxios} from "@/util/http-common";

const local = localAxios();

async function addSheetToOrderAPI(sheetIds, payType, totalPrice, successCallback, errorCallback) {
    try {
        const response = await local.post(`/order`, {
            sheetIds: sheetIds,
            payType: payType,
            totalPrice: totalPrice,
        });
        successCallback(response.data);
    } catch (error) {
        errorCallback(error);
    }

}

async function getOrderSheetsAPI(success, fail) {
    await local.get(`/order`).then(success).catch(fail);
}

export {
    addSheetToOrderAPI,
    getOrderSheetsAPI,
}