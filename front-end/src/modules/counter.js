export const INITIAL = 'initial';

export default (state = {}, action) => {
  switch (action.type) {
    case INITIAL:
      return {
        ...state
      }

    default:
      return state;
  }
}
