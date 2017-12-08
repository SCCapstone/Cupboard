import React, { Component } from 'react';
import {
  StyleSheet,
  Text,
  View,
  Alert
} from 'react-native';
import { FormLabel, FormInput, Button} from 'react-native-elements';
import { style } from './Styles';
import FirebaseHandler from './FirebaseHandler';

class SignIn extends Component {
  constructor(props) {
    super(props);
    this.state = {
      text: this.props.credential
    };
  }

  extract(text) {
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
            this.extract(text);
          }}
          value={this.state.text}
          onFocus={() => {
            if (this.state.text === this.props.credential) {
              this.setState({
                text: ""
              })
            }
          }}
          secureTextEntry={this.props.secure}
        />
      </View>
    );
  }
}

export default class LoginScreen extends Component<{}> {

  constructor(props) {
    super(props);

    this.state = {
      username: '',
      password: '',
      fbhandler: new FirebaseHandler()
    }
  }

  // Sign in the email and password
  signIn() {
    console.log(this.state);
    this.state.fbhandler.signIn(this.state.username, this.state.password, ()=> {
      this.props.navigation.navigate('HomeS', {
        'fbhandler': this.state.fbhandler
      });
    }, (err) => {
      Alert.alert("Error signing in!");
    });
  }

  debugSignIn(email, password) {
    this.state.fbhandler.signIn(email, password, (user)=> {
      this.props.navigation.navigate('HomeS', {
        'fbhandler': this.state.fbhandler
      });
    }, (err) => {
      Alert.alert("Error signing in debug!");
    });
  }

  // Create a new user
  createUser() {
    this.state.fbhandler.createUser(this.state.username, this.state.password, () =>{
      this.props.navigation.navigate('HomeS', fbhandler);
    }, (err) => {
      Alert.alert("Error creating user!");
    })
  }

  render() {
    const { navigate } = this.props.navigation;
    return (
      <View style={style.containerCenterContent}>
        <Text style={style.title}>
          Welcome to Cupboard!
        </Text>
        <SignIn credential={"Email"} self={this} secure={false}/>
        <SignIn credential={"Password"} self={this} secure={true}/>
        <View style={style.buttons}>
          <Button
            containerViewStyle={style.buttonContainer}
            buttonStyle={style.button}
            backgroundColor="white"
            onPress={this.signIn.bind(this)}
            title="SIGN IN"
            raised
            //color="black"
          />
          <Button
            containerViewStyle={style.buttonContainer}
            buttonStyle={style.button}
            backgroundColor="#875F9A"
            onPress={() => {
              this.createUser.bind(this)();
            }}
            title="CREATE ACCOUNT"
            color="white"
            raised
          />
          <Button
            containerViewStyle={style.buttonContainer}
            buttonStyle={style.button}
            onPress={()=> {
             this.debugSignIn("username@example.com", "password");
            }}
            title="DEBUG"
            color="white"
            backgroundColor="#9D2933"
            raised
          />
        </View>
      </View>
    );
  }
}