#ifndef _SKILL_H_
#define _SKILL_H_

#include "preDeclare.h"

class skill
{
public:
	bool beTriggled();
protected:
private:
	bool _isLocked; // ������
	string _name;
};

#endif