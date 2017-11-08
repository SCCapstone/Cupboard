/**
 * Sample React Native App
 * https://github.com/facebook/react-native
 * @flow
 */

import React, { Component } from 'react';
import {
  Platform,
  StyleSheet,
  Text,
  View,
  TextInput
} from 'react-native';

const instructions = Platform.select({
  ios: 'Press Cmd+R to reload,\n' +
    'Cmd+D or shake for dev menu',
  android: 'Double tap R on your keyboard to reload,\n' +
    'Shake or press menu button for dev menu',
});

class SignIn extends Component {
  constructor(props) {
    super(props);
    this.state = {
      text: this.props.credential
    };
  }

  render() {
    return (
      <TextInput
        style={{height: 40, width: '75%', marginBottom: 10, borderColor: 'gray', borderWidth: 1}}
        onChangeText={(text) => this.setState({text})}
        onFocus={(text) => this.setState({text: ''})}
        value={this.state.text}
      />
    );
  }
}

export default class App extends Component<{}> {
  render() {
    return (
      <View style={styles.container}>
        <Text style={styles.welcome}>
          Welcome to
          <Text style={{fontWeight: 'bold'}}> Cupboard!</Text>
        </Text>
        <SignIn credential={"Username"}/>
        <SignIn credential={"Password"}/>
      </View>
    );
  }
}

const styles = StyleSheet.create({
  container: {
    flex: 1,
    justifyContent: 'center',
    alignItems: 'center',
    backgroundColor: '#F5FCFF',
  },
  welcome: {
    fontSize: 20,
    textAlign: 'center',
    marginBottom: 20
  },
  instructions: {
    textAlign: 'center',
    color: '#333333',
    marginBottom: 5,
  },
});
