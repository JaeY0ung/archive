import Swal from "sweetalert2";

const showLoginRequestAlert = (router) => {
    Swal.fire({
            icon: 'info',
            title: '로그인 후 이용 가능합니다.',
            text: '로그인 하시겠습니까?',
            showCancelButton: true,
            confirmButtonText: '예', 
            cancelButtonText: '아니오',
            confirmButtonColor: '#429f50',
            cancelButtonColor: '#d33',
        }).then(result => {
            if (result.isConfirmed) {
                router.push({ name: 'login' });
            }
        })
}

const showAgainRegisterAlert = (router, sheetId) => {
    Swal.fire({
        icon: 'info',
        title: '계속 업로드하시겠습니까?',
        showCancelButton: true,
        confirmButtonText: '예', 
        cancelButtonText: '아니오',
        confirmButtonColor: '#429f50',
        cancelButtonColor: '#d33',
    }).then(result => {
        if (!result.isConfirmed) {
            router.push({ name: "sheetDetail", params: { sheetId } });
        }
    })
}

const showUnselectedWarningAlert = (router, warning) => {
    Swal.fire({
        icon: 'info',
        title: warning,
        confirmButtonText: '예', 
        confirmButtonColor: '#429f50',
    })
}

export {
    showLoginRequestAlert,
    showAgainRegisterAlert,
    showUnselectedWarningAlert,
}