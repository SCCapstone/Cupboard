/*import { StyleSheet } from 'react-native';

const style = StyleSheet.create({
  containerCenterContent: {
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
  },
  buttonContainer: {
    alignSelf: "center",
    marginBottom: 5,
    width: "auto",
    borderRadius: 10,
  },
  logoutButtonContainer: {
    position: 'absolute',
    bottom:0,
    left:0,
    marginBottom: 5,
    width: "auto",
    borderRadius: 10,
  },
  buttons: {
    flexDirection: "row"
  },
  accordianButtons: {
    flexDirection: "row",
    alignSelf: "center",
  },
  accordianHeader: {
    flex: 1,
    flexDirection: "row",
    justifyContent: "center",
    alignItems: "center"
  },
  smallerTextInput: {
    borderWidth: 1,
    width: 30
  },
  listElement: {
    flexDirection: "row",
  }
});

export { style }

*/

import { StyleSheet } from 'react-native';

const backgroundColor_ = 'white';
const buttonColor_ = '#1D7874';
//const textColor_ = '#413133';
//const borderColor_ = '#99621E';


const style = StyleSheet.create({
  containerCenterContent: {
    flexDirection: "column",
    justifyContent: "center",
    alignItems: "center",
    height: "100%",
    backgroundColor: backgroundColor_
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
    backgroundColor: buttonColor_,

  },
  buttonContainer: {
    alignSelf: "center",
    marginBottom: 5,
    width: "auto",
    borderRadius: 10,
  },
  logoutButtonContainer: {
    position: 'absolute',
    bottom:5,
    left:0,
  },
  buttons: {
    flexDirection: "row"
  },
  accordianButtons: {
    flexDirection: "row",
    alignSelf: "center",
  },
  accordianHeader: {
    flex: 1,
    flexDirection: "row",
    justifyContent: "center",
    alignItems: "center"
  },
  smallerTextInput: {
    borderWidth: 1,
    width: 30
  },
  listElement: {
    flexDirection: "row",
  }
});

export { style }