const getTitleByLen = (name, len) => {
    if (name.length > len) return name.slice(0, len) + "...";
    return name;
}

const copyText = (copyText) => {
    window.navigator.clipboard.writeText(copyText).then(() => {
        alert("주소 복사 완료!");
    });
}

export { getTitleByLen, copyText };