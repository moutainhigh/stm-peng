package com.mainsteam.stm.lock.obj;


/** 
 * @author 作者：ziw
 * @date 创建时间：2017年2月3日 下午4:46:40
 * @version 1.0
 */
public class LockRequestNode<E> {
    E item;
    LockRequestNode<E> next;
    LockRequestNode<E> prev;

    public LockRequestNode(LockRequestNode<E> prev, E element, LockRequestNode<E> next) {
        this.item = element;
        this.next = next;
        this.prev = prev;
    }
    
    public E getEtem(){
    	return item;
    }
}
