const stringUtil = {
    getTitleByLen(name, len) {
        if (name.length > len) return name.slice(0, len) + "...";
        return name;
    },
    
    copyText(copyText) {
        window.navigator.clipboard.writeText(copyText).then(() => {
            alert("주소 복사 완료!");
        });
    }
};

export default stringUtil;