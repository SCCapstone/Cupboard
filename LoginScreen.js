import React, { Component } from 'react';
import {
  StyleSheet,
  Text,
  View,
  Alert,
  Image
} from 'react-native';
import { FormLabel, FormInput, Button} from 'react-native-elements';
import { style } from './Styles';
import FirebaseHandler from './FirebaseHandler';

const remote = 'https://vignette.wikia.nocookie.net/beauxbatonsacademy/images/0/07/Iphone-wallpaper-bookshelf.jpg/revision/latest?cb=20130307035455';

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
    this.state.fbhandler.signIn(this.state.username, this.state.password, ()=> {
      this.props.navigation.navigate('HomeS', fbhandler);
    }, (err) => {
      Alert.alert(err);
    });
  }

  debugSignIn(email, password) {
    this.state.fbhandler.signIn(email, password, (user)=> {
      this.props.navigation.navigate('HomeS', {
        'fbhandler': this.state.fbhandler
      });
    }, (err) => {
      Alert.alert(err);
    });
  }

  // Create a new user
  createUser() {
    this.state.fbhandler.createUser(this.state.username, this.state.password, () =>{
      this.props.navigation.navigate('HomeS', fbhandler);
    }, (err) => {
      Alert.alert(err);
    })
  }

  render() {
    const { navigate } = this.props.navigation;
    const resizeMode = 'cover';
    //const text = 'Welcome to Cupboard!';

    return (
      <View style={style.containerCenterContent}>
        <View
            style={{
                position: 'absolute',
                top: 0,
                left: 0,
                width: '100%',
                height: '100%',
            }}
        >
          <Image
              style={{
                  flex: 1,
                  resizeMode,
              }}
              source={{ uri: remote }}
          />
        </View>
        <View>
        <Text style={style.title}>
          Welcome to Cupboard!
        </Text>
        </View>
        <SignIn credential={"Email"} self={this} secure={false}/>
        <SignIn credential={"Password"} self={this} secure={true}/>
        <View style={style.buttons}>
          <Button
            containerViewStyle={style.buttonContainer}
            buttonStyle={style.button}
            backgroundColor="#b8b09b"
            onPress={this.signIn.bind(this)}
            title="SIGN IN"
            color="black"
            raised
          />
          <Button
            containerViewStyle={style.buttonContainer}
            buttonStyle={style.button}
            backgroundColor="#dbd7cc"
            onPress={() => {
              this.createUser.bind(this)();
            }}
            title="CREATE ACCOUNT"
            color="black"
            raised
          />
          <Button
            containerViewStyle={style.buttonContainer}
            buttonStyle={style.button}
            onPress={()=> {
             this.debugSignIn("username@example.com", "password");
            }}
            title="DEBUG"
            color="black"
            backgroundColor="#948869"
            raised
          />
        </View>
      </View>
    );
  }
}