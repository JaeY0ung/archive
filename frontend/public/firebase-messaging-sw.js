if ('serviceWorker' in navigator) { 
    navigator.serviceWorker.register('../firebase-messaging-sw.js') 
    .then(function(registration) { 
      console.log('등록 성공, 범위는 다음과 같습니다.', registration.scope); 
    }).catch(function(err) { 
      console.log('서비스 워커 등록에 실패했습니다. 오류:', err); 
    }); 
  }