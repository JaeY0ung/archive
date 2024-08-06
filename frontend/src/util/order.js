// src/utils/cart.js
export const getOrder = () => {
    const order = localStorage.getItem('order');
    return order ? JSON.parse(order) : [];
};

export const addToOrder = (item) => {
    const order = getOrder();
    order.push(item);
    localStorage.setItem('order', JSON.stringify(order));
};

export const removeFromOrder = (itemId) => {
    const order = getOrder();
    const updatedOrder = order.filter(item => item.id !== itemId);
    localStorage.setItem('order', JSON.stringify(updatedOrder));
};

export const clearOrder = () => {
    localStorage.removeItem('order');
};
