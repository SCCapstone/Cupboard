import React, { Component } from 'react';
import {
  Text,
  View,
  StyleSheet,
  TextInput,
  Alert,
  ScrollView,
  FlatList,
  TouchableOpacity
} from 'react-native';
import { Button, Icon } from 'react-native-elements';
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
          data: []
      };
      this.onAddToList = this.onAddToList.bind(this);
      this.EditItem = this.EditItem.bind(this);
      this.deleteItem = this.deleteItem.bind(this);
  }

  static navigationOptions = ({navigation}) => ({
    headerRight:
      <TouchableOpacity style={{flexDirection: "row"}}
                        onPress={() => navigation.navigate('EntryS', {
        'fbhandler': navigation.state.params.fbhandler,
        'prevScreen': navigation.state.params.self
      })}>
        <Icon name="plus"
              type="entypo"
              color="#739E82"
              containerStyle={{
                marginRight: 15
              }}
        />
      </TouchableOpacity>
  });

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

  // Deletes a food item from Firebase, then updates local data
  deleteItem(item) {
      const fbhandler = this.props.navigation.state.params.fbhandler;
      const ref = fbhandler.deleteFood(item.key);

      let newData = this.state.data;
      newData.splice(this.state.data.indexOf(item), 1);

      this.setState({
          data: newData
      });
  }

  // Edits Quantity
  // Should we keep this as an onChanged method, or turn into onSubmit?
  onChanged(text,foodid){
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
      // Save edit in Firebase
      const fbhandler = this.props.navigation.state.params.fbhandler;
      fbhandler.saveFoodQuantity(foodid, parseInt(newText, 10));

      // Set state locally
      this.setState({ noItems: newText });
  }

  // Loads current foods from Firebase
  async _refreshFoods(){
    const fbhandler = this.props.navigation.state.params.fbhandler;
    const foods = await fbhandler.getFoods();

    const data = [];

    if (foods.val()){
      Object.keys(foods.val()).forEach(function(key) {
        data.push({
          title: foods.val()[key].title,
          noItems: foods.val()[key].noItems.toString(),
          content: foods.val()[key].content,
          key: key
        });
      });

      this.setState({
        data: data
      });

    } else {
      this.setState({
        data: []
      });
    }
  }

  // Firebase function to get foods from database, set it to data
  async componentDidMount(){
    // Workaround to use THIS in header
    this.props.navigation.setParams({
      self: this
    });

    this._refreshFoods();
  }

  render() {
    return (
      <View style={style.content}>
        <ScrollView>
          <SearchBar
            round
            lightTheme
            //onChangeText={filterResults}
            //onClearText={}
            placeholder='Search' />
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
                          onChangeText={(text)=> this.onChanged(text,item.key)}
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
                          //color="black"
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
                            onPress={()=>{
                              this.props.navigation.navigate("EditFoodS", {
                                food: item,
                                fbhandler: this.props.navigation.state.params.fbhandler,
                                prevScreen: this
                              });
                            }}
                            /*
                            onPress={
                              this.EditItem
                            }
                            */
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