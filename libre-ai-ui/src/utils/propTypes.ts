import type { CSSProperties, VNodeChild } from 'vue';
import {
  createTypes,
  toValidableType,
  type VueTypesInterface,
  type VueTypeValidableDef
} from 'vue-types';

export type VueNode = VNodeChild | JSX.Element;

type PropTypes = VueTypesInterface & {
  readonly style: VueTypeValidableDef<CSSProperties>;
  readonly VNodeChild: VueTypeValidableDef<VueNode>;
};

const newPropTypes = createTypes({
  func: undefined,
  bool: undefined,
  string: undefined,
  number: undefined,
  object: undefined,
  integer: undefined
}) as PropTypes;

// 扩展 propTypes 对象
const propTypes = Object.assign(newPropTypes, {
  get style() {
    return toValidableType('style', {
      type: [String, Object]
    });
  },
  get VNodeChild() {
    return toValidableType('VNodeChild', {
      type: undefined
    });
  }
}) as PropTypes;

export { propTypes };
export default propTypes;
