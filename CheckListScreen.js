import React, { Component } from 'react';
import {
  Text,
  View,
  StyleSheet,
  ListView,
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

  _onChange(text){
    const {fbhandler, list, self} = this.props.params;

    const id = fbhandler.user.uid;
    const listid = list.key;

    const itemid = this.props.id;

    let alldata = this.props.self.state.data;

    alldata.map((elem)=>{
      if (elem.id === itemid){
        elem.title = text;
        return elem;
      }
    });

    this.props.self.setState({
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
          onPress={() => this.setState({
            checked: !this.state.checked
          })}
        />
        <FormInput
          containerStyle={{
            width: "66%"
          }}
          value={this.state.title}
          onChangeText={(text)=>{
            this._onChange(text);
          }}
        />
      </View>
    );
  }
}

export default class CheckListScreen extends Component<{}> {
  constructor(props) {
    super(props);

    this.state = {
      data: null,
    };
  }

  static navigationOptions = ({ navigation }) => ({
    title: navigation.state.params.list.title,
    headerRight:
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

    fbhandler.saveListElements(listid, json);
  }

  async componentDidMount(){
    this.props.navigation.setParams({
      self: this
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
        <Button
          onPress={()=>{this.addToList()}}
          title={"add element"}
        />
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