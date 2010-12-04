#ifndef _CARD_H_
#define _CARD_H_

#include "preDeclare.h"

enum CARD_SUIT
{
	CS_HEART,		// ��
	CS_CLUB,		// ÷
	CS_DIAMOND,		// ��
	CS_SPADE,		// ��
};

enum CARD_TYPE
{
	CT_TRICK,		// ������
	CT_DELAY_TRICK, // �ӳٽ�����
	CT_EQUIPMENT,	// װ����
	CT_NORMAL,		// ��ͨ��
};

enum CARD_SUB_TYPE
{
	// ����
	CST_WXKJ,	// ��и�ɻ�
	CST_SSQY,	// ˳��ǣ��
	CST_WZSY,	// ��������
	CST_WGFD,	// ��ȷ��

	// ��ʱ����
	CST_SD,		// ����
	CST_LBSS,	// �ֲ�˼��

	// װ��
	CST_BGZ, // ������
	CST_QLD, // �������µ�
	CST_DH,  // +1��
	CST_OH,  // -1��

	// ��ͨ
	CST_SHA,  // ��
	CST_SHAN, // ɱ
	CST_TAO,  // ��
};



class card
{
public:
	card(string name, CARD_SUIT suit, int num, CARD_TYPE type, CARD_SUB_TYPE subType);

	virtual void operate() = 0;
protected:
private:
	CARD_SUIT _suit;
	CARD_TYPE _type;
	CARD_SUB_TYPE _subType;
	int _num;
	string _name;
	bool _isTmp;
};

#endif // _CARD_H_