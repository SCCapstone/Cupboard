import React, { Component } from 'react';
import {
  Text,
  View,
  StyleSheet
} from 'react-native';
import { Button } from 'react-native-elements'

export default class HomeScreen extends Component<{}> {
  render() {
    return (
      <View style={styles.page}>
        <Button
          containerViewStyle={styles.buttonContainer}
          buttonStyle={styles.button}
          title='MY CUPBOARD'
          onPress={() => {
            this.props.navigation.navigate("CupboardS");
          }}
        />
        <Button
          title='RECIPES'
          containerViewStyle={styles.buttonContainer}
          buttonStyle={styles.button}
          onPress={() => {
            this.props.navigation.navigate("RecipesS");
          }}
        />
        <Button
          title='SHOPPING LISTS'
          containerViewStyle={styles.buttonContainer}
          buttonStyle={styles.button}
          onPress={() => {
            this.props.navigation.navigate("ListsS");
          }}
        />
      </View>
    );
  }
}

const styles = StyleSheet.create({
  page: {
    flexDirection: "column",
    justifyContent: "center",
    alignItems: "center",
    height: "100%",
  },
  title: {
    fontSize: 19,
    fontWeight: 'bold',
  },
  activeTitle: {
    color: 'red',
  },
  button: {
    borderRadius: 10,
    padding: 25,
  },
  buttonContainer: {
    alignSelf: "center",
    marginBottom: 30,
    width: 200,
    borderRadius: 10,
  },
  buttons: {
    flexDirection: "row"
  }
});