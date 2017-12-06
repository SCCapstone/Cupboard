import React, { Component } from 'react';
import {
  Text,
  View,
  StyleSheet,
  Alert
} from 'react-native';
import { Button } from 'react-native-elements';
import { style } from "./Styles";

export default class HomeScreen extends Component<{}> {

  constructor(props) {
    super(props);
  }

  render() {
    const navigation = this.props.navigation;
    const fbhandler = navigation.state.params.fbhandler;

    return (
      <View style={style.containerCenterContent}>

        <Text>Hello {fbhandler.user.email}!</Text>
        <Button
          containerViewStyle={style.buttonContainer}
          buttonStyle={style.button}
          title='MY CUPBOARD'
          onPress={() => {
            navigation.navigate("CupboardS");
          }}
        />
        <Button
          title='RECIPES'
          containerViewStyle={style.buttonContainer}
          buttonStyle={style.button}
          onPress={() => {
            navigation.navigate("RecipesS");
          }}
        />
        <Button
          title='SHOPPING LISTS'
          containerViewStyle={style.buttonContainer}
          buttonStyle={style.button}
          onPress={() => {
            navigation.navigate("ListsS", {
              'fbhandler': fbhandler
            });
          }}
        />
        <Button
          title='SIGN OUT'
          containerViewStyle={style.buttonContainer}
          buttonStyle={style.button}
          onPress={()=>{
            fbhandler.signOut(()=>{
              navigation.goBack();
            }, (err)=> {
              Alert.alert(err);
            });
          }}
        />
      </View>
    );
  }
}