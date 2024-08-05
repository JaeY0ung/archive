import {localAxios} from "@/util/http-common";

const local = localAxios();

async function addSheetToOrderAPI(sheetIds, payType, success, fail) {
    console.log("sheetIds-----", sheetIds);
    console.log("paymentType---------", payType);
    await local.post(`/order`, {
        sheetIds: sheetIds,
        payType: payType,
    }).then(success).catch(fail);
}

async function getOrderSheetsAPI(success, fail) {
    await local.get(`/order`).then(success).catch(fail);
}

export {
    addSheetToOrderAPI,
    getOrderSheetsAPI,
}