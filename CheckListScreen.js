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
  _onCheckChange(){
    const self = this.props.self;
    const itemid = this.props.id;
    let alldata = self.state.data;

    this.setState({
      checked: !this.state.checked
    }, ()=> {
      alldata.map((elem)=>{
        if (elem.id === itemid){
          elem.checked = this.state.checked;
          return elem;
        }
      });

      self.setState({
        data: alldata
      });
    });
  }

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

    self.setState({
      data: alldata
    });

    this.setState({
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
            padding: 0
          }}
          checked={this.state.checked}
          onPress={() => {
            this._onCheckChange();
          }}
        />
        <FormInput
          containerStyle={{
            width: "66%"
          }}
          value={this.state.title}
          onChangeText={(text)=>{
            this._onTextChange(text);
          }}
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
      data: null,
      title: this.props.navigation.state.params.list.title
    };
  }

  static navigationOptions = ({ navigation }) => ({
    headerTitle: <View>
      <HeaderInput
        title={navigation.state.params.list.title}
        self={navigation.state.params.self}
      />
    </View>,
    headerRight: <View style={{ flexDirection: "row"}}>
      <TouchableOpacity
        onPress={()=>{
          navigation.state.params.self.addToList();
        }}
      >
        <Icon
          color="#739E82"
          name="plus"
          type="entypo"
          containerStyle={{
            marginRight: 20
          }}
        />
      </TouchableOpacity>
      <TouchableOpacity
        onPress={()=>{
          navigation.state.params.self.saveList();
          Alert.alert("List Saved!");
        }}
      >
        <Icon
          color="#739E82"
          name="save"
          type="entypo"
          containerStyle={{
            marginRight: 20
          }}
        />
      </TouchableOpacity>
    </View>
  });

  addToList() {
    const fbhandler = this.props.navigation.state.params.fbhandler;
    const listid = this.props.navigation.state.params.list.key;

    const ref = fbhandler.addListItemToList(listid);

    let newData = this.state.data;
    newData.push({
      id: ref.key,
      title: "",
      checked: false
    });

    this.setState({
      data: newData
    });
  }

  saveList() {
    const fbhandler = this.props.navigation.state.params.fbhandler;
    const listid = this.props.navigation.state.params.list.key;

    const data = this.state.data;
    const json = {};

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

  async componentDidMount(){
    const self = this;
    this.props.navigation.setParams({
      self: self,
      title: this.state.title
    });

    const fbhandler = this.props.navigation.state.params.fbhandler;
    const listid = this.props.navigation.state.params.list.key;

    const listItems = await fbhandler.getListItems(listid);

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
      this.setState({
        data: []
      });
    }
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