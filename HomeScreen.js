import React, { Component } from 'react';
import {
  Text,
  View,
  StyleSheet,
  Alert,
  Image,
} from 'react-native';
import { Button } from 'react-native-elements';
import { style } from "./Styles";

const remote = 'http://www.retinaiphonewallpapers.com/wp-content/uploads/00023.jpg';


export default class HomeScreen extends Component<{}> {

  constructor(props) {
    super(props);
  }

  render() {
    const navigation = this.props.navigation;
    const fbhandler = navigation.state.params.fbhandler;
    const resizeMode = 'cover';


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
            //TODO fix img
              <Image
                  style={{
                      flex: 1,
                      resizeMode,
                  }}
                  source={{ uri: remote }}
              />
        </View>
        <View>
        <Text>Hello {fbhandler.user.email}!</Text>
        </View>
        <View style={style.buttons}>
          <Button
            containerViewStyle={style.buttonContainer}
            buttonStyle={style.button}
            backgroundColor="#b8b09b"
            title="MY CUPBOARD"
            color="black"
            raised
            onPress={() => {
              navigation.navigate("CupboardS", {
                'fbhandler': fbhandler
              });
            }}
          />
          <Button
            containerViewStyle={style.buttonContainer}
            buttonStyle={style.button}
            backgroundColor="#b8b09b"
            title="RECIPES"
            color="black"
            raised
            onPress={() => {
              navigation.navigate("RecipesS");
            }}
          />
          <Button
            containerViewStyle={style.buttonContainer}
            buttonStyle={style.button}
            backgroundColor="#b8b09b"
            title="SHOPPING LISTS"
            color="black"
            onPress={() => {
              navigation.navigate("ListsS", {
                'fbhandler': fbhandler
              });
            }}
          />
          <Button
            containerViewStyle={style.logoutButtonContainer}
            buttonStyle={style.button}
            backgroundColor="#dbd7cc"
            title="SIGN OUT"
            color="black"
            raised
            onPress={()=>{
              fbhandler.signOut(()=>{
                navigation.goBack();
              }, (err)=> {
                Alert.alert(err);
              });
            }}
          />
        </View>
      </View>
    );
  }
}