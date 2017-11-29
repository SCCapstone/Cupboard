import { StyleSheet } from 'react-native';

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
  buttons: {
    flexDirection: "row"
  },
  listElement: {
    flexDirection: "row"
  }
});

export { style }