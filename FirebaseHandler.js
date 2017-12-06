import firebase from 'react-native-firebase';

const firebaseConfig = {
  apiKey: 'AIzaSyDGvm-2kkreAPcffUHWCH5HaWlHas6Cnkg',
  authDomain: 'the-cupboard-app.firebaseapp.com',
  databaseURL: 'https://the-cupboard-app.firebaseio.com/',
  storageBucket: 'gs://the-cupboard-app.appspot.com',
  appId: '1:449840930413:android:7f854998b9cb29a1',
  messagingSenderId: '449840930413',
  projectId: 'the-cupboard-app'
};

const firebaseApp = firebase.initializeApp(firebaseConfig);

export default class FirebaseHandler {
  constructor() {
    this.firebase = firebaseApp;
    this.user = null;
  }

  // create a user
  createUser(email, password, callback, callbackErr){
    this.firebase.auth().createUserWithEmailAndPassword(email, password)
      .then( (user) => {
        this.user = user;
        callback(user);
      }).catch( (err) => {
        if (callbackErr) {
          callbackErr(err);
        }
      }
    );
  }

  // sign user in
  signIn(email, password, callback, callbackErr) {
    this.firebase.auth().signInWithEmailAndPassword(email, password)
      .then( (user) => {
        this.user = user;
        callback(user);
      }).catch( (err) => {
      if (callbackErr) {
        callbackErr(err);
      }
    });
  }

  // sign user out
  signOut(callback, callbackErr) {
    this.firebase.auth().signOut()
      .then((user) => {
        this.user = null;
        callback(user);
      }).catch( (err) => {
        callbackErr(err);
    });
  }

  // create a list for the current user.
  // returns the ref.
  createList(listname) {
    return this.firebase.database().ref('lists/' + this.user.uid).push({
      title: listname
    });
  }

  // get lists
  // returns a promise.
  getLists(){
    const ref = this.firebase.database().ref('lists/' + this.user.uid);
    return ref.once("value");
  }

  // get lists items
  // returns a promise.
  getListItems(listid){
    const ref = this.firebase.database().ref('lists/' + this.user.uid + '/' + listid + '/items');
    return ref.once("value");
  }

  addListItemToList(listid){
    return this.firebase.database().ref('lists/' + this.user.uid + '/' + listid + '/items').push({
      title: "",
      checked: false
    });
  }

  saveListElements(listid, json){
    return firebaseApp.database().ref('lists/' + this.user.uid + '/' + listid + '/items').update(json);
  }

  saveListTitle(listid, title) {
    return firebaseApp.database().ref('lists/' + this.user.uid + '/' + listid).update({title: title});
  }

  deleteList(listid){
    const ref = this.firebase.database().ref('lists/' + this.user.uid + '/' + listid);
    return ref.remove();
  }

  deleteItemFromList(listid, itemid){
    const ref = this.firebase.database().ref('lists/' + this.user.uid + '/' + listid + '/items/' + itemid);
    return ref.remove();
  }

  // get foods
  // returns a promise.
  getFoods(){
    const ref = this.firebase.database().ref('foods/' + this.user.uid);
    return ref.once("value");
  }

  // create a food for the current user.
  // name is a string, quantity is an int, content is a string
  // returns the ref.
  addFood(name, quantity, content){
    return this.firebase.database().ref('foods/' + this.user.uid).push({
      title: name,
      noItems: quantity,
      content: content
    });
  }

  // deletes food item given a food id
  // food id is a key
  deleteFood(foodid){
    const ref = this.firebase.database().ref('foods/' + this.user.uid + '/' + foodid);
    return ref.remove();
  }
}