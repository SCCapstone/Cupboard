import React, { Component } from 'react';
import {
  Text,
  View,
  StyleSheet
} from 'react-native';
import { style } from './Styles'
import { Button } from 'react-native-elements'
import Accordion from 'react-native-collapsible/Accordion'
//https://www.npmjs.com/package/react-native-collapsible
import { SearchBar } from 'react-native-elements'
//https://react-native-training.github.io/react-native-elements/API/searchbar/


//TODO:: put in manual entry button in the header
//TODO:: put in search functionality
//TODO:: implement deleting of rows
//TODO:: add 'edit item' and 'add to list' button functionality
//TODO:: Read in data from user/firebase to SECTIONS
//TODO:: Look into accordian separators


const SECTIONS = [ //PLACEHOLDER. THIS WILL BE READ IN FROM USER/FIREBASE DATA.
  {
    title: 'Cereal',
    content: 'Calories - 250\nExpires - 10/20/17',
  },
  {
    title: 'Baked beans',
    content: 'Calories - 20\nExpires - 10/21/17',
  }
];

export default class CupboardScreen extends Component<{}> {
  constructor(props) {
    super(props);
      this.state = {
        quantity: 1
      };
      this.onIncrementPress = this.onIncrementPress.bind(this)
      this.onAddToList = this.onAddToList.bind(this)
      this.EditItem = this.EditItem.bind(this)
  }

  /*
  filterResults() {
    //TODO:: add filtering of list for search function
  }
  */
  onIncrementPress() {
    this.setState({quantity: 2}) //this was supposed to increment the quantity of an item, but doesnt work.
    //It's something to do with asynchronicity and callback functions...
  }

  onAddToList() {
    //TODO:: implement this function
  }

  EditItem() {
    //TODO:: implement this function
  }

  _renderHeader(section) {
    return (
      <View style={styles.title}>
        <Text>{section.title}</Text>
        <Button
            containerViewStyle={style.buttonContainer}
            buttonStyle={style.button}
            backgroundColor="#ffffff"
            onPress={
              this.onIncrementPress
            }
            title="Increment"
            color="black"
            raised
        />
      </View>
    );
  }

  _renderContent(section) {
    return (
      <View style={styles.containerCenterContent}>
        <Text>{section.content}</Text>
          <View style={style.accordianButtons}>
          <Button
              containerViewStyle={style.buttonContainer}
              buttonStyle={style.button}
              backgroundColor="#ffffff"
              onPress={
                  this.onAddToList
              }
              title="Add to List"
              color="black"
              raised
          />
          <Button
          containerViewStyle={style.buttonContainer}
          buttonStyle={style.button}
          backgroundColor="#ffffff"
          onPress={
              this.EditItem
          }
          title="Edit Item"
          color="black"
          raised
      />
          </View>
      </View>
    );
  }

  render() {
    return (
      <View style={styles.content}>
      <SearchBar
        round
        //onChangeText={filterResults}
        //onClearText={}
        placeholder='Type Here...' />
      <Accordion
        sections={SECTIONS}
        renderHeader={this._renderHeader}
        renderContent={this._renderContent}
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
});