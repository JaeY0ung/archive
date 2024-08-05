import {localAxios} from "@/util/http-common";

const local = localAxios();

async function addSheetToCartAPI(sheetId, success, fail) {
    await local.post(`/cart/${sheetId}`).then(success).catch(fail);
}

async function getCartItemsAPI(success, fail) {
    await local.get(`/cart`).then(success).catch(fail);
}

export {
    addSheetToCartAPI,
    getCartItemsAPI,
}