import React, { Component } from 'react';
import {
  Text,
  View,
  StyleSheet,
  ListView,
  TextInput,
  FlatList,
  TouchableOpacity,
  Alert,
  ScrollView
} from 'react-native';
import { Button, CheckBox, FormInput, Icon} from 'react-native-elements'
import { style } from "./Styles";

class ListElement extends Component {
  constructor(props) {
    super(props);

    this.state = {
      checked: this.props.checked,
      title: this.props.title,
    };
  }


  // TODO make onCheckChange and onTextChange be the same method
  // Every time something is checked or unchecked, it updates in the main component.
  _onCheckChange(){
    const self = this.props.self;
    const itemid = this.props.id;
    let alldata = self.state.data;

    this.setState({ // update own state
      checked: !this.state.checked
    }, ()=> {
      alldata.map((elem)=>{
        if (elem.id === itemid){
          elem.checked = this.state.checked;
          return elem;
        }
      });

      self.setState({ // update the main components state
        data: alldata
      });
    });
  }

  // deletes an item for the main components list
  _delete(){
    this.props.self.deleteFromList(this.props.id);
  }

  // Every time text is edited, it updates in the main component.
  _onTextChange(text){
    const self = this.props.self;
    const itemid = this.props.id;
    let alldata = self.state.data;

    alldata.map((elem)=>{
      if (elem.id === itemid){
        elem.title = text;
        return elem;
      }
    });

    self.setState({ // update the main components state
      data: alldata
    });

    this.setState({ // update own state
      title: text
    });
  }

  render() {
    return (
      <View style={style.listElement}>
        <CheckBox
          containerStyle={{
            backgroundColor: "rgba(0, 0, 0, 0)",
            margin: 10,
            marginLeft: 30,
            marginRight: 0,
            padding: 2,
            borderWidth: 0
          }}
          checked={this.state.checked}
          onPress={() => {
            this._onCheckChange();
          }}
        />
        <FormInput // item text input
          containerStyle={{
            width: "66%"
          }}
          value={this.state.title}
          onChangeText={(text)=>{
            this._onTextChange(text);
          }}
        />
        <Icon // button for deleting items
          color="#413133"
          name="x"
          type="feather"
          containerStyle={{
          }}
          onPress={()=>{
            this._delete();
          }}
          underlayColor={'transparent'}
        />
      </View>
    );
  }
}

class HeaderInput extends Component {
  constructor(props) {
    super(props);

    this.state = {
      title: this.props.title
    };
  }

  _onChange(text) {
    this.setState({
      title: text
    },()=>{
      this.props.self.setState({
        title: text
      });
    });
  }

  render() {
    return (<FormInput
      underlineColorAndroid="transparent"
      inputStyle={{
        color: "black",
        fontWeight: "bold",
        fontSize: 18
      }}
      containerStyle={{
        width: 200,
      }}
      onChangeText={(text)=>{
        this._onChange(text);
      }}
      value={this.state.title}
    />)
  }
}

export default class CheckListScreen extends Component<{}> {
  constructor(props) {
    super(props);

    this.state = {
      data: null, // array of data objects
      title: this.props.navigation.state.params.list.title // title of the list
    };
  }

  // header options
  static navigationOptions = ({ navigation }) => ({
    headerTitle: <View>
      <HeaderInput
        title={navigation.state.params.list.title}
        self={navigation.state.params.self}
      />
    </View>,
    headerRight: <View style={{ flexDirection: "row"}}>
      <Icon
        color="#739E82"
        name="plus"
        type="entypo"
        containerStyle={{
          marginRight: 20
        }}
        onPress={()=>{
          navigation.state.params.self.addToList();
        }}
      />
      <Icon
        color="#739E82"
        name="save"
        type="entypo"
        containerStyle={{
          marginRight: 20
        }}
        onPress={()=>{
          navigation.state.params.self.saveList();
          Alert.alert("List Saved!");
        }}
      />
    </View>
  });

  // creates an empty list item and adds to firebase + current state
  addToList() {
    const fbhandler = this.props.navigation.state.params.fbhandler;
    const listid = this.props.navigation.state.params.list.key;

    const ref = fbhandler.addListItemToList(listid);

    // add data to temporary data array
    let newData = this.state.data;
    newData.push({
      id: ref.key,
      title: "",
      checked: false
    });

    // set state as new data array
    this.setState({
      data: newData
    });
  }

  // deletes an item from list
  async deleteFromList(itemid) {
    const fbhandler = this.props.navigation.state.params.fbhandler;
    const listid = this.props.navigation.state.params.list.key;

    await fbhandler.deleteItemFromList(listid, itemid);

    // get data and remove object that matches itemid
    let newData = this.state.data;
    newData = newData.filter((elem)=>{
      return elem.id !== itemid;
    });

    // set state as new data array
    this.setState({
      data: newData
    });
  }

  // saves the current state of the list, including title.
  saveList() {
    const fbhandler = this.props.navigation.state.params.fbhandler;
    const listid = this.props.navigation.state.params.list.key;

    const data = this.state.data;
    const json = {};

    // parse data array into firebase acceptable json format
    data.forEach((elem) => {
      json[elem.id] = {
        checked: elem.checked,
        title: elem.title
      };
    });

    // save title
    fbhandler.saveListTitle(listid, this.state.title);
    // save elements
    fbhandler.saveListElements(listid, json);
  }

  // whenever the component mounts, it gathers the list items and populates the data state
  async componentDidMount(){

    // workaround for getting an editable title into the header
    const self = this;
    this.props.navigation.setParams({
      self: self,
      title: this.state.title
    });

    const fbhandler = this.props.navigation.state.params.fbhandler;
    const listid = this.props.navigation.state.params.list.key;

    const listItems = await fbhandler.getListItems(listid);

    // parse firebase json data into an usable data array format.
    const data = [];
    if (listItems.val()){
      Object.keys(listItems.val()).forEach(function(key) {
        data.push({
          title: listItems.val()[key].title,
          checked: listItems.val()[key].checked,
          id: key
        });
      });
      this.setState({
        data: data
      });
    } else {
      // if there are no list items for this list, set to empty array
      this.setState({
        data: []
      });
    }
  }

  // every time the component unmounts, save the list and refresh on the previous screen.
  componentWillUnmount(){
    this.saveList();
    this.props.navigation.state.params.prevScreen._refreshLists(); // this is passing data backwards (kinda)
  }

  render() {
    return (
      <View>
        <FlatList
          data={this.state.data}
          renderItem={({item}) => <ListElement
            params={this.props.navigation.state.params}
            checked={item.checked}
            title={item.title}
            id={item.id}
            self={this}
          />}
          keyExtractor={(item, index) => item.id}
          extraData={this.state}
        />
      </View>
    );
  }
}