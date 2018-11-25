const admin = require('firebase-admin');
const node_cron = require('node-cron');
const date_and_time = require('date-and-time');
var gcm = require('node-gcm');

var serviceAccount = require('./serviceaccountkey.json');

admin.initializeApp({
  credential: admin.credential.cert(serviceAccount)
});
var db = admin.firestore();

// Set up the sender with your GCM/FCM API key (declare this once for multiple messages)
var sender = new gcm.Sender('AIzaSyBmGC-rZ5FrYoUciEinoURSKRi6GUz_HJ4');


var user_observer = db.collection('users').onSnapshot(querySnapshot => {
  querySnapshot.docChanges.forEach(change => {
    if (change.type === 'added') {
      addTaskObserver(change.doc);
    }
  });
});

function addTaskObserver(doc) {
  return doc.onSnapshot(querySnapshot => {
    querySnapshot.docChanges.forEach(change => {
      if (change.type === 'added') {
        var owner = change.doc.get('owner');
        var deadline = change.doc.get('deadline').toDate();
        var text = change.doc.get('text');
        node_cron.schedule(getCronTimeString(deadline), () => {
          if (change.doc.get('completed') == false)
            sendMsg(text, owner);
        });
      }
    });
  });
}

function sendMsg(msg, owner) {
  // Prepare a message to be sent
  var message = new gcm.Message({
    data: { key1: text }
  });
  // Specify which registration IDs to deliver the message to
  var regTokens = [db.collection('users').doc(owner).get('token')];
  // Actually send the message
  sender.send(message, { registrationTokens: regTokens }, function (err, response) {
    if (err) console.error(err);
    else console.log(response);
  });
}

function getCronTimeString(d) {
  return '* ' + d.minutes + ' ' + d.hours + ' ' + d.day + ' ' + d.monthIndex;
}