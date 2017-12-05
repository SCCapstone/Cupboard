import React, { Component } from 'react';
import {
  Text,
  View,
  StyleSheet,
  TextInput,
  Alert,
  ScrollView,
  FlatList
} from 'react-native';
import { Button } from 'react-native-elements';
import Accordion from 'react-native-collapsible/Accordion';
//https://www.npmjs.com/package/react-native-collapsible
import { SearchBar } from 'react-native-elements';
import { style } from './Styles';
//https://react-native-training.github.io/react-native-elements/API/searchbar/

//TODO:: put in search functionality
//TODO:: add 'edit item' and 'add to list' button functionality
//TODO:: Read in data from user/firebase to SECTIONS
//TODO:: Look into accordian separators
//TODO:: add "sort by" button at the top

export default class CupboardScreen extends Component<{}> {
  constructor(props) {
    super(props);
      this.state = {
          data: [
      {
          title: 'Cereal1',
          noItems: '2',
          content: 'Calories - 250\nExpires - 10/20/17',
          key: '1'
      },
      {
          title: 'Baked beans',
          noItems: '3',
          content: 'Calories - 20\nExpires - 10/21/17',
          key: '2'
      },
      {
          title: 'Cereal2',
          noItems: '2',
          content: 'Calories - 250\nExpires - 10/20/17',
          key: '3'
      },
      {
          title: 'Cereal3',
          noItems: '2',
          content: 'Calories - 250\nExpires - 10/20/17',
          key: '4'
      },
      {
          title: 'Cereal4',
          noItems: '2',
          content: 'Calories - 250\nExpires - 10/20/17',
          key: '5'
      },
      {
          title: 'Cereal5',
          noItems: '2',
          content: 'Calories - 250\nExpires - 10/20/17',
          key: '6'
      },
      {
          title: 'Cereal6',
          noItems: '2',
          content: 'Calories - 250\nExpires - 10/20/17',
          key: '7'
      },
              ]
      };
      this.onAddToList = this.onAddToList.bind(this);
      this.EditItem = this.EditItem.bind(this);
      this.deleteItem = this.deleteItem.bind(this);
  }

  /*
  filterResults() {
    //TODO:: add filtering of list for search function
  }
  */

  onAddToList() {
    //TODO:: implement this function
  }

  EditItem() {
    //edit item should be the exact same as manual entry, just renamed to 'edit a food' rather than 'add a food'
    //TODO:: implement this function
  }

  deleteItem(item) {
      let newData = this.state.data;
      newData.splice(this.state.data.indexOf(item), 1);

      this.setState({
          data: newData
      });
  }

  onChanged(text,arrayvar){
      let newText = '';
      let numbers = '0123456789';
      for (let i=0; i < text.length; i++) {
          if(numbers.indexOf(text[i]) > -1 ) {
              newText = newText + text[i];
          }
          else {
              // your call back function
              alert("please enter numbers only");
          }
      }
      this.setState({ noItems: arrayvar });
  }

  render() {
    return (
      <View style={style.content}>
        <ScrollView>
          <SearchBar
            round
            //onChangeText={filterResults}
            //onClearText={}
            placeholder='Type Here...' />
          <FlatList
            data={this.state.data}
            renderItem={({item}) =>
              <Accordion
                sections={['Section 1']}
                renderHeader={()=>
                  {
                    let arrayvar = item.noItems
                    return (
                      <View style={style.accordianHeader}>
                        <Text style={style.accordianHeader}>{item.title}</Text>
                        <TextInput
                          style={style.smallerTextInput}
                          keyboardType='numeric'
                          onChangeText={(text)=> this.onChanged(text,arrayvar)}
                          placeholder={arrayvar}
                          maxLength={2}  //setting limit of input
                        />
                        <Button //SHOULD BE SWIPE TO DELETE, IS BUTTON FOR NOW
                          containerViewStyle={style.buttonContainer}
                          buttonStyle={style.button}
                          backgroundColor="#ffffff"
                          onPress={() => {
                            this.deleteItem(item)
                          }}

                          title="Delete Item"
                          color="black"
                          raised
                        />
                    </View>
                    );
                  }
                }
                renderContent={()=>
                  {
                    return (
                      <View style={style.containerCenterContent}>
                        <Text style={style.accordianHeader}>{item.content}</Text>
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
                }
              />}
            keyExtractor={(item) => item.key}
            extraData={this.state}
          />
        </ScrollView>
      </View>
    );
  }
}