importScripts('https://www.gstatic.com/firebasejs/8.6.1/firebase-app.js');
importScripts('https://www.gstatic.com/firebasejs/8.6.1/firebase-messaging.js');

firebase.initializeApp({
  apiKey: 'VUE_APP_FIREBASE_API_KEY',
  authDomain: 'VUE_APP_FIREBASE_AUTH_DOMAIN',
  projectId: 'VUE_APP_FIREBASE_PROJECT_ID',
  storageBucket: 'VUE_APP_FIREBASE_STORAGE_BUCKET',
  messagingSenderId: 'VUE_APP_FIREBASE_MESSAGING_SENDER_ID',
  appId: 'VUE_APP_FIREBASE_APP_ID',
  measurementId: 'VUE_APP_FIREBASE_MEASUREMENT_ID'
});

const messaging = firebase.messaging();

messaging.onBackgroundMessage((payload) => {
  console.log('백그라운드 메시지를 받았습니다. ', payload);
  const notificationTitle = payload.notification.title;
  const notificationOptions = {
    body: payload.notification.body,
    icon: payload.notification.icon
  };

  self.registration.showNotification(notificationTitle, notificationOptions);
});
