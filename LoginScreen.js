import React, { Component } from 'react';
import {
    StyleSheet,
    Text,
    View,
    Alert
} from 'react-native';
import firebase from 'react-native-firebase';
import { StackNavigator } from 'react-navigation';
import { FormLabel, FormInput, Button} from 'react-native-elements'

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

class SignIn extends Component {
    constructor(props) {
        super(props);
        this.state = {
            text: this.props.credential
        };
    }

    tempfunction(text) {
        if (this.props.credential === "Email") {
            this.props.self.setState({username: text})
        } else {
            this.props.self.setState({password: text});
        }
    }

    render() {
        return (
            <View>
              <FormLabel>
                {this.props.credential}
              </FormLabel>
              <FormInput
                onChangeText={(text) => {
                  this.setState({text});
                  this.tempfunction(text);
                }}
                value={this.state.text}
                onFocus={(text) => this.setState({text: ''})}
                secureTextEntry={this.props.secure}
              />
            </View>
        );
    }
}

export default class LoginScreen extends Component<{}> {

    static navigationOptions = {
      header: null
    };

    constructor(props) {
        super(props);

        this.state = {
            username: '',
            password: ''
        }
    }

    signIn() {
        firebase.auth().signInWithEmailAndPassword(this.state.username, this.state.password)
            .then((user) => {
                Alert.alert('Signed in successfully!');
            }).catch( (err) => {

            Alert.alert('Incorrect log in!');
        });
    }

    createUser() {
        firebase.auth().createUserWithEmailAndPassword(this.state.username, this.state.password)
            .then((user) => {
                Alert.alert('Created new account!');
            }).catch( (err) => {
            console.log(err);
            Alert.alert('Unable to create account.');
        });
    }

    render() {
        const { navigate } = this.props.navigation;
        return (
            <View style={styles.page}>
                <Text style={styles.title}>
                  Welcome to Cupboard!
                </Text>
                <SignIn credential={"Email"} self={this} secure={false}/>
                <SignIn credential={"Password"} self={this} secure={true}/>
                <View style={styles.buttons}>
                  <Button
                    containerViewStyle={styles.buttonContainer}
                    buttonStyle={styles.button}
                    backgroundColor="white"
                    onPress={this.signIn.bind(this)}
                    title="SIGN IN"
                    raised
                    color="black"
                  />
                  <Button
                    containerViewStyle={styles.buttonContainer}
                    buttonStyle={styles.button}
                    backgroundColor="#875F9A"
                    onPress={() => {
                      this.createUser.bind(this)
                      navigate('HomeS')
                    }}
                    title="CREATE ACCOUNT"
                    color="white"
                    raised
                  />
                  <Button
                    containerViewStyle={styles.buttonContainer}
                    buttonStyle={styles.button}
                    onPress={() => {navigate('HomeS')}}
                    title="HOME"
                    color="white"
                    backgroundColor="#9D2933"
                    raised
                  />
                </View>

            </View>
        );
    }
}

const styles = StyleSheet.create({
  page: {
    flexDirection: "column",
    justifyContent: "center",
    alignItems: "center",
    height: "80%",
  },
  title: {
    fontSize: 19,
    fontWeight: 'bold',
  },
  activeTitle: {
    color: 'red',
  },
  button: {
    borderRadius: 10
  },
  buttonContainer: {
    alignSelf: "center",
    marginBottom: 5,
    width: "auto",
    borderRadius: 10
  },
  buttons: {
    flexDirection: "row"
  }
});

